# Force.com Tooling API Demo

This is a simple Spring MVC web application that implements a very simple Force.com code browser/editor. The demo allows developers to read, edit, and (soon) create and delete Apex classes.

## Getting Started

1. _Note - since the Tooling API is not generally available until [Spring '13](http://developer.force.com/releases/release/Spring13), you will need access to a Spring '13 sandbox or pre-release org_. [Sign up for pre-release access here](https://www.salesforce.com/form/signup/prerelease-spring13.jsp).
2. Setup your salesforce.com OAuth Remote Access. You will then have a OAuth client key and secret
3. Update the Environment variables to include the OAuth endpoint (for the pre-release, this will be `https://prerellogin.pre.salesforce.com`), client key and secret
4. Navigate to the "classes" page and you should now be authenticated against Salesforce and be able to view/update classes
