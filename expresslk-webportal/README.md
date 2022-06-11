ExpressLK Webportal
========================

Introduction
------------
This is the webportal of ExpressLK. 
Done in PHP with Zend Framework 2 in the back end, 
AngularJs on front end scripting and
Bootstrap 3 as css framework.

Grunt has been used as a task runner to minify js/css/images, build, deploy and other tasks

Installation
------------

### Apache Setup and Initialize project

1. To setup apache, setup a virtual host to point to the public/ directory of the
project and you should be ready to go! It should look something like below:
  
  
    <VirtualHost *:80>
        ServerName expresslk.loc
        DocumentRoot /path/to/zf2/public
        SetEnv APPLICATION_ENV "development"
        <Directory /path/to/zf2/public>
            DirectoryIndex index.php
            AllowOverride All
            Order allow,deny
            Allow from all
        </Directory>
    </VirtualHost>
    
2. add ``` expresslk.loc ``` into hosts file and point to 127.0.0.1
    
3. cd into root directory and execute composer install

    ```
    php composer.phar install
    ```
### Node Js, Grunt and other tools for developments

1. Install [node js, NPM](http://nodejs.org/) and [grunt](http://gruntjs.com/getting-started)
2. cd into root directory and execute

    ```
    npm install
    ```

3. Install sass [http://sass-lang.com/install](http://sass-lang.com/install)
4. Install css importer to combine scss and css ```gem install --pre sass-css-importer```
5. In the same place enter command ``` bower install ```
6. Copy grunt-settings/deploy-secret.js.sample => grunt-settings/deploy-secret.js
7. Run ``` grunt ```
8. If you are changing any css/js Run 

    ```
    grunt watch
    ```
    before changing files. This will watch for changes

Re-generate SOAP classes
------------------------
### (!To be executed only on change) Search Web service - Generate classes from wsdl
Garage
php vendor/wsdl2phpgenerator/wsdl2phpgenerator/wsdl2php -i http://167.114.96.6:7575/ExpressLK-search/search?wsdl -o module/Api/src/Api/Client/Soap/Core -n 'Api\Client\Soap\Core'
php vendor/wsdl2phpgenerator/wsdl2phpgenerator/wsdl2php -i http://167.114.96.6:7575/ExpressLK-reports/reports?wsdl -o module/Api/src/Api/Client/Soap/Core -n 'Api\Client\Soap\Core'

Depot
php vendor/wsdl2phpgenerator/wsdl2phpgenerator/wsdl2php -i http://158.69.202.65:7070/ExpressLK-search/search?wsdl -o module/Api/src/Api/Client/Soap/Core -n 'Api\Client\Soap\Core'
php vendor/wsdl2phpgenerator/wsdl2phpgenerator/wsdl2php -i http://158.69.202.65:7070/ExpressLK-reports/reports?wsdl -o module/Api/src/Api/Client/Soap/Core -n 'Api\Client\Soap\Core'
