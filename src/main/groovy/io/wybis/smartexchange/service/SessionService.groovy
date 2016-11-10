package io.wybis.smartexchange.service

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.dto.UserDto
import io.wybis.smartexchange.model.Account
import io.wybis.smartexchange.model.User
import io.wybis.smartexchange.service.exceptions.GeneralException
import io.wybis.smartexchange.service.exceptions.InvalidCredentialException
import io.wybis.smartexchange.service.exceptions.ModelAlreadyExistException
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException

import javax.servlet.http.HttpSession

public interface SessionService {

    static String SESSION_USER_KEY = 'user'

    static String SESSION_USER_ID_KEY = 'userId'

    static String SESSION_USER_PASSWORD_KEY = 'userPassword'

    static String SESSION_LOGIN_REDIRECT_KEY = 'loginRedirectKey'

    boolean validateRecaptcha(String recaptchaValue)

    Map<String, Object> properties(HttpSession session)

    String signUp(UserDto userDto, String domainPrefix) throws ModelAlreadyExistException, GeneralException

    String resetPasswordRequest(UserDto userDto, String domainPrefix) throws ModelNotFoundException, GeneralException

    void signIn(HttpSession session, UserDto userDto)
            throws InvalidCredentialException

    void signOut(HttpSession session)

    void changeDetails(SessionDto sessionUser, UserDto userDto)
            throws ModelNotFoundException

    void changePassword(SessionDto sessionUser, UserDto userDto)
            throws ModelNotFoundException, GeneralException

    List<User> employees(SessionDto sessionUser)

    List<User> customers(SessionDto sessionUser)

    List<Account> stocks(SessionDto sessionUser)

    List<Account> ledgers(SessionDto sessionUser)

}
