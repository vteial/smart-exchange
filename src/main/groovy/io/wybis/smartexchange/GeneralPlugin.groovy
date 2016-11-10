package io.wybis.smartexchange

import groovyx.gaelyk.plugins.PluginBaseScript
import io.wybis.smartexchange.service.impl.DefaultAccountService
import io.wybis.smartexchange.service.impl.DefaultAutoNumberService
import io.wybis.smartexchange.service.impl.DefaultBranchService
import io.wybis.smartexchange.service.impl.DefaultCustomerService
import io.wybis.smartexchange.service.impl.DefaultEmployeeService
import io.wybis.smartexchange.service.impl.DefaultProductService
import io.wybis.smartexchange.service.impl.DefaultSessionService
import io.wybis.smartexchange.service.impl.DefaultUserService

class GeneralPlugin extends PluginBaseScript {

    @Override
    public Object run() {
        log.info "Registering GeneralPlugin started..."

        DefaultAutoNumberService anS = new DefaultAutoNumberService()

        DefaultAccountService actS = new DefaultAccountService()
        actS.autoNumberService = anS

        DefaultProductService prdS = new DefaultProductService()
        prdS.autoNumberService = anS
        prdS.accountService = actS

        DefaultUserService usrS = new DefaultUserService()
        usrS.autoNumberService = anS

        DefaultEmployeeService empS = new DefaultEmployeeService()
        empS.autoNumberService = anS
        empS.accountService = actS

        DefaultCustomerService cusS = new DefaultCustomerService()
        cusS.autoNumberService = anS
        cusS.accountService = actS

        DefaultBranchService bchS = new DefaultBranchService()
        bchS.autoNumberService = anS
        bchS.accountService = actS
        bchS.productService = prdS
        bchS.employeeService = empS
        bchS.customerService = cusS

//        DefaultOrderService ordS = new DefaultOrderService()
//        ordS.autoNumberService = anS
//        ordS.accountService = actS
//
//        DefaultTranService trnS = new DefaultTranService()
//        trnS.autoNumberService = anS
//        trnS.orderService      = ordS
//
//        DefaultTransferService tfrS = new DefaultTransferService()
//        tfrS.autoNumberService = anS

        DefaultSessionService sesS = new DefaultSessionService()
        sesS.autoNumberService = anS
        sesS.accountService = actS
        sesS.userService = usrS
        sesS.appUserService = users

        binding {
            systemout         = System.out
            jsonCategory      = JacksonCategory
            jsonObjectMapper  = JacksonCategory.jsonObjectMapper
            autoNumberService = anS
            sessionService    = sesS
            branchService     = bchS
            accountService    = actS
            productService    = prdS
            sesS.userService  = usrS
            employeeService   = empS
            customerService   = cusS
//            orderService      = ordS
//            tranService       = trnS
//            transferService   = tfrS
        }

//        routes {
//        }
//
//        before {
//            if(servletContext[Helper.DUMP_REQUEST_RESPONSE_KEY]) {
//                Helper.dumpRequest(request)
//            }
//        }
//
//        after {
//            if(servletContext[Helper.DUMP_REQUEST_RESPONSE_KEY]) {
//                Helper.dumpResponse(response)
//            }
//        }

        log.info "Registering GeneralPlugin finished..."
    }
}
