# Force.com Tooling API Demo

This is a simple Spring MVC web application that implements a very simple Force.com code browser/editor. The demo allows developers to create, read, update and delete Apex classes.

## Getting Started

1. Setup your salesforce.com OAuth Remote Access. Use `https://myappname.herokuapp.com/_auth` as the callback URL. You will then have a OAuth client key and secret.
2. Update the environment variables to include the OAuth endpoint (for production, this will be `https://login.salesforce.com`; for sandbox, `https://test.salesforce.com`), client key and secret:

		export SFDC_OAUTH_CLIENT_ID=YOUR_CLIENT_ID
		export SFDC_OAUTH_CLIENT_SECRET=YOUR_CLIENT_SECRET
		export SFDC_OAUTH_ENDPOINT=A_LOGIN_ENDPOINT

3. Navigate to the "classes" page and you should now be authenticated against Salesforce and be able to create/read/update/delete classes.
