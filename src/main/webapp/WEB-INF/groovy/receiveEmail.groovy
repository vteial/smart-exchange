
def msg = mail.parseMessage(request)
//systemout.println(msg)
//log.info "Subject ${msg.subject}, to ${msg.allRecipients.join(', ')}, from ${msg.from[0]}"

def s = """Received Mail Message
	From    : ${msg.from[0]}
	To      : ${msg.allRecipients.join(', ')}
	Subject : ${msg.subject}
"""
log.info s

def payload = [
	from 	: 'smartexchange@appspot.com',
	to 		: msg.from[0],
	subject : 'Re : ' + msg.subject,
	body 	: 'Your request acknowledged...'
]
def payloadJson = new groovy.json.JsonBuilder(payload)
def payloadJsonString = payloadJson.toString()

def defaultQueue = queues.default
defaultQueue << [
	url      : '/taskEmail.groovy',
	taskName : 'taskEmail',
	method   : 'POST',
	payload  : payloadJsonString
]