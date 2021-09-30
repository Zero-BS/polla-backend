exports.authorizer = async function (event) {
    const methodArn = event.methodArn;
    if (!event.authorizationToken
        || typeof event.authorizationToken !== 'string'
        || !event.authorizationToken.startsWith('Bearer '))
        return generateAuthResponse('', 'Deny', methodArn);

    const token = event.authorizationToken.substring('Bearer '.length);
    let principal = await verify(token);

    return generateAuthResponse(principal, 'Allow', methodArn);
}

function generateAuthResponse(principal, effect, methodArn) {
    const policyDocument = generatePolicyDocument(effect, methodArn);

    return {
        principal,
        policyDocument
    };
}

function generatePolicyDocument(effect, methodArn) {
    if (!effect || !methodArn) return null;

    const policyDocument = {
        Version: '2012-10-17',
        Statement: [{
            Action: 'execute-api:Invoke',
            Effect: effect,
            Resource: methodArn
        }]
    };

    return policyDocument;
}

const { OAuth2Client } = require('google-auth-library');
const client = new OAuth2Client(process.env.ANDROID_GOOGLE_CLIENT_ID);
async function verify(token) {
    const ticket = await client.verifyIdToken({
        idToken: token,
        audience: process.env.ANDROID_GOOGLE_CLIENT_ID
    });
    console.log(ticket);
    const payload = ticket.getPayload();
    console.log(payload);
    return payload;
}