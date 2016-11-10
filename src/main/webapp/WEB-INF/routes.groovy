
email						to : '/receiveEmail.groovy'

get 	'/favicon.ico',		redirect : '/images/favicon.png'

get     '/',				redirect : '/index'
get     '/index',			forward  : '/index.groovy'
get     '/console',			forward  : '/console.groovy'
get 	'/info',			forward  : '/info.groovy'
get 	'/build-info',		forward  : '/build-info.groovy'
get		'/ping',			forward  : '/ping.groovy'
get		'/forbidden',		forward  : '/forbidden.groovy'
all 	'/_ah/warmup',		forward  : '/ping.groovy'

// console
get 	'/console/reset',    					            forward : '/io/wybis/smartexchange/web/console/reset.groovy'
get 	'/console/branchs',    					            forward : '/io/wybis/smartexchange/web/console/branchs.groovy'
post 	'/console/branchs/branch',        		            forward : '/io/wybis/smartexchange/web/console/branchAdd.groovy'
post 	'/console/branchs/branch/@id/products',             forward : '/io/wybis/smartexchange/web/console/branchProductsAdd.groovy?branchId=@id'
post 	'/console/branchs/branch/@id/employees',            forward : '/io/wybis/smartexchange/web/console/branchEmployeesAdd.groovy?branchId=@id'
//post 	'/console/branchList/branch/@id/dealers',              forward : '/io/wybis/smartexchange/web/console/branchDealersAdd.groovy?branchId=@id'
post 	'/console/branchs/branch/@id/customers',            forward : '/io/wybis/smartexchange/web/console/branchCustomersAdd.groovy?branchId=@id'
get 	'/console/branchs/branch/@id/reset',	            forward : '/io/wybis/smartexchange/web/console/branchReset.groovy?branchId=@id'
delete 	'/console/branchs/branch/@id', 			            forward : '/io/wybis/smartexchange/web/console/branchDelete.groovy?branchId=@id'

// session
get  	'/sessions/properties',	                            forward : '/io/wybis/smartexchange/web/session/properties.groovy'

post 	'/sessions/sign-up',     	                        forward : '/io/wybis/smartexchange/web/session/signUp.groovy'
get 	'/sessions/sign-up-confirm',                        forward : '/io/wybis/smartexchange/web/session/signUpConfirm.groovy'
post 	'/sessions/reset-password-request',                 forward : '/io/wybis/smartexchange/web/session/resetPasswordRequest.groovy'
get 	'/sessions/reset-password-confirm',                 forward : '/io/wybis/smartexchange/web/session/resetPasswordConfirm.groovy'
post 	'/sessions/reset-password',                         forward : '/io/wybis/smartexchange/web/session/resetPassword.groovy'

post 	'/sessions/sign-in',     	                        forward : '/io/wybis/smartexchange/web/session/signIn.groovy'
get  	'/sessions/sign-out',    	                        forward : '/io/wybis/smartexchange/web/session/signOut.groovy'
post 	'/sessions/change-details',     	                forward : '/io/wybis/smartexchange/web/session/changeDetails.groovy'
post 	'/sessions/change-password',     	                forward : '/io/wybis/smartexchange/web/session/changePassword.groovy'

