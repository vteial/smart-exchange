package io.wybis.smartexchange.service.impl

import groovyx.gaelyk.GaelykBindings
import groovyx.gaelyk.logging.GroovyLogger
import io.wybis.smartexchange.JacksonCategory
import io.wybis.smartexchange.dto.RecaptchaResponseDto
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.dto.UserDto
import io.wybis.smartexchange.model.Account
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Role
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.model.constants.AccountType
import io.wybis.smartexchange.model.constants.UserStatus
import io.wybis.smartexchange.service.AccountService
import io.wybis.smartexchange.service.SessionService
import io.wybis.smartexchange.service.UserService
import io.wybis.smartexchange.service.exceptions.*

import javax.servlet.http.HttpSession

@GaelykBindings
public class DefaultSessionService extends AbstractService implements
        SessionService {

    GroovyLogger log = new GroovyLogger(DefaultSessionService.class.getName())

    AccountService accountService

    UserService userService

    Map<String, Object> app = [:]

    com.google.appengine.api.users.UserService appUserService

    @Override
    boolean validateRecaptcha(String recaptchaValue) {
        RecaptchaResponseDto recaptchaResponseDto = null

        URL url = new URL('https://www.google.com/recaptcha/api/siteverify')
        def params = [secret: '6Lfg9CkTAAAAAJDl6ttpIGdrg2mNg0-3oUg6z24d', response: recaptchaValue]
        def response = url.post(params: params)
        if (response.statusCode != 200) {
            log.severe(response.text)
            log.severe('Recaptcha verifiction failed...')
        }
        if (localMode) {
            System.out.println(response.text)
        }
        recaptchaResponseDto = JacksonCategory.jsonObjectMapper.readValue(response.text, RecaptchaResponseDto.class)
        if (localMode) {
            System.out.println(recaptchaResponseDto)
        }

        return recaptchaResponseDto.success
    }

    @Override
    public Map<String, Object> properties(HttpSession session) {
        def props = this.app.clone()

        props.localMode = localMode
        props.sessionDto = session.getAttribute(SESSION_USER_KEY)
        props.sessionId = session.id
        props.applicationUser = user

        return props;
    }

    @Override
    String signUp(UserDto userDto, String domainPrefix) throws ModelAlreadyExistException, GeneralException {
        if (!userDto.recaptchaValue) {
            throw new GeneralException('Missing recaptcha value...')
        }

        if (!this.validateRecaptcha(userDto.recaptchaValue)) {
            throw new GeneralException('Invalid recaptcha value...')
        }

        User auser = new User()
        auser.with {
            userId = userDto.userId
            password = userDto.password
            firstName = userDto.firstName
            lastName = userDto.lastName
        }
        if (!userDto.emailId) {
            auser.emailId = userDto.userId
        }

        SessionDto sessionUser = new SessionDto()
        userService.create(sessionUser, auser)

        return this.sendSignUpEmail(auser, domainPrefix)
    }

    private String sendSignUpEmail(User auser, String domainPrefix) {
        log.info('sign up email started...')

        String confirmUrl = "${domainPrefix}/sessions/sign-up-confirm?userId=${auser.id}&userToken=${auser.token}"

        String mailContent = """
Hi ${auser.firstName},

    Thanks for sign up. Please use this link ${confirmUrl} to confirm your email account.

Thanks and Regards,
App Admin,
smartexchange.appspot.com
"""
        mail.send(from: 'vteial@gmail.com',
                to: auser.emailId,
                subject: "SurveyMonster - Account Confirmation",
                textBody: mailContent)

        log.info("confirmation url is ${confirmUrl}")
        log.info('sign up email finished...')

        return confirmUrl
    }

    @Override
    String resetPasswordRequest(UserDto userDto, String domainPrefix) throws ModelNotFoundException, GeneralException {
        if (!userDto.recaptchaValue) {
            throw new GeneralException('Missing recaptcha value...')
        }

        if (!this.validateRecaptcha(userDto.recaptchaValue)) {
            throw new GeneralException('Invalid recaptcha value...')
        }

        userDto.userId = userDto.userId.toLowerCase();
        def dsl = datastore.build {
            from User.class.simpleName
            where userId == userDto.userId
            limit 1
        }
        def entitys = dsl.execute()
        if (entitys.size() == 0) {
            throw new ModelNotFoundException()
        }

        User auser = entitys[0] as User
        auser.token = UUID.randomUUID().toString()
        auser.save()

        return this.sendResetPasswordEmail(auser, domainPrefix)
    }

    private String sendResetPasswordEmail(User auser, String domainPrefix) {
        log.info('reset password email started...')

        String resetUrl = "${domainPrefix}/sessions/reset-password-confirm?userId=${auser.id}&userToken=${auser.token}"

        String mailContent = """
Hi ${auser.firstName},

    Please use this link ${resetUrl} to reset password for your account.

Thanks and Regards,
App Admin,
smartexchange.appspot.com
"""
        mail.send(from: 'vteial@gmail.com',
                to: auser.emailId,
                subject: "SurveyMonster Reset Password",
                textBody: mailContent)

        log.info("reset url is ${resetUrl}")
        log.info('reset password email finished...')

        return resetUrl
    }

    public void signIn(HttpSession session, UserDto userDto)
            throws InvalidCredentialException {

        def entitys = datastore.execute {
            from User.class.simpleName
            where userId == userDto.userId
            limit 1
        }

        if (entitys.size() == 0) {
            throw new InvalidCredentialException()
        }
        User aUser = entitys[0] as User

        if (aUser.status == UserStatus.PENDING || aUser.status == UserStatus.PASSIVE) {
            throw new UnAuthorizedException()
        }

        if (!localMode && aUser.password != userDto.password) {
            throw new InvalidCredentialException()
        }

        SessionDto sessionDto = new SessionDto()
        sessionDto.with {
            id = aUser.id
            userId = aUser.userId
            firstName = aUser.firstName
            lastName = aUser.lastName
            type = aUser.type
            roleId = aUser.roleId
            cashAccountId = aUser.cashAccountId
            profitAccountId = aUser.profitAccountId
            branchId = aUser.branchId
        }
        Branch branch = Branch.get(sessionDto.branchId)
        sessionDto.branchCode = branch.code
        sessionDto.branchName = branch.name
        sessionDto.branchVirtualEmployeeId = branch.virtualEmployeeId

        session.setAttribute(SESSION_USER_KEY, sessionDto)

    }

    @Override
    public void signOut(HttpSession session) {
        session.removeAttribute(SESSION_USER_KEY)
    }

    @Override
    public void changeDetails(SessionDto sessionUser, UserDto userDto)
            throws ModelNotFoundException {
        User euser = User.get(sessionUser.id)

        euser.firstName = userDto.firstName
        euser.lastName = userDto.lastName

        euser.preUpdate(sessionUser.id)
        euser.save()

        sessionUser.firstName = euser.firstName
        sessionUser.lastName = euser.lastName
    }

    @Override
    public void changePassword(SessionDto sessionUser, UserDto userDto)
            throws ModelNotFoundException, GeneralException {
        User euser = User.get(sessionUser.id)

        if (!euser) {
            throw new ModelNotFoundException()
        }

        if (euser.password != userDto.currentPassword) {
            throw new GeneralException('Invalid current password...')
        }

        if (userDto.newPassword != userDto.retypeNewPassword) {
            throw new GeneralException('New password and Retype new password should be equal...')
        }

        if (euser.password == userDto.newPassword) {
            throw new GeneralException('Current password and New password should not be equal...')
        }

        euser.password = userDto.newPassword

        euser.preUpdate(sessionUser.id)
        euser.save()
    }

    @Override
    public List<User> employees(SessionDto sessionUser) {
        List<User> models = []

        //		if(sessionUser.roleId == Role.ID_MANAGER) {
        //			def entitys = datastore.execute {
        //				from User.class.simpleName
        //				where branchId == sessionUser.branchId
        //				and type == UserType.EMPLOYEE
        //			}
        //
        //			entitys.each { entity ->
        //				User model = entity as User
        //				model.cashAccount = Account.get(model.cashAccountId)
        //				model.profitAccount = Account.get(model.profitAccountId)
        //				models <<  model
        //			}
        //		}
        //		else {
        //			User model = User.get(sessionUser.id)
        //			model.cashAccount = Account.get(model.cashAccountId)
        //			model.profitAccount = Account.get(model.profitAccountId)
        //			models << model
        //		}

        models = this.userService.findByBranchIdAndType(sessionUser.branchId, UserType.EMPLOYEE)

        return models;
    }

    @Override
    public List<User> customers(SessionDto sessionUser) {
        List<User> models = null

        models = this.userService.findByBranchIdAndType(sessionUser.branchId, UserType.CUSTOMER)

        return models;
    }

    @Override
    public List<Account> stocks(SessionDto sessionUser) {
        List<Account> models = null

        models = accountService.findByUserIdAndType(sessionUser.id, AccountType.PRODUCT)

        return models;
    }

    @Override
    public List<Account> ledgers(SessionDto sessionUser) {
        List<Account> models = null

        List<String> accountTypes = []
        if (sessionUser.roleId == Role.ID_MANAGER) {
            accountTypes << AccountType.CASH_CAPITAL
            accountTypes << AccountType.CASH_EMPLOYEE
            accountTypes << AccountType.PROFIT_EMPLOYEE
            models = accountService.findByBranchIdAndTypes(sessionUser.branchId, accountTypes)
        } else {
            accountTypes << AccountType.CASH_EMPLOYEE
            accountTypes << AccountType.PROFIT_EMPLOYEE
            models = accountService.findByUserIdAndTypes(sessionUser.id, accountTypes)
        }

        return models;
    }

}
