=== Video First Starter (${config.baseUrl})

VF Starter creates projects for VFA (Video First Automation) - see https://github.com/videofirst/vfa for more info.

The primary API for generating application is: -

    ${config.baseUrl}/{project}.zip

=== Examples

To create a default application:

    $ curl ${config.baseUrl}/example.zip -o example.zip

To create with full parameters

    $ curl ${config.baseUrl}/example.zip?prefix=true \
        &package=com.mycompany \
        &description=Demo%20project%20for%20VFA \
        -o example.zip

Parameters available are: -

 * prefix      - Value is "true" / "false". If "true", the project files in ZIP file will exist in a folder (the folder
                 will be the project name). Select "false" if a folder already exists (e.g. for an existing GIT repo).
 * package     - Base package e.g. "com.mycompany"
 * description - Short description of project e.g. "Demo project for VFA"

Prefer a user interface to create your project? If so, go to ${config.baseUrl} using your browser.
