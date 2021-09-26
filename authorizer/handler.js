exports.authorizer = async function (event) {
    const methodArn = event.methodArn;
    if (!event.authorizationToken
        || typeof event.authorizationToken !== 'string'
        || !event.authorizationToken.startsWith('Bearer '))
        return generateAuthResponse('', 'Deny', methodArn);

    const token = event.authorizationToken.substring('Bearer '.length);

    return generateAuthResponse(token, 'Allow', methodArn);
}

function generateAuthResponse(principalId, effect, methodArn) {
    const policyDocument = generatePolicyDocument(effect, methodArn);

    return {
        principalId,
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