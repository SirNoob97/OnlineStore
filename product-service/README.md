# Product-Service

Microservice for products management.

## Execution Commands

TEST spring and maven profile

    mvn clean compile spring-boot:run -Dspring-boot.run.profiles=test -P test

POSTGRESQL spring and maven profile

    mvn clean compile spring-boot:run -Dspring-boot.run.profiles=postgresql -P postgresql

## Endpoints

If you are using docker you should use the respective IP of this microservice instead of "localhost"

    192.168.0.12

### Products

GET a page with 10 elements of the ProductListView type, discount to avoid sending information to users who do not need to know

    curl -v -X GET http://localhost:8091/products\?page\=0\&size\=10 -H 'Accept: application/json' | jq

GET, like the previous command but only of products that belong to foo main category

    curl -v -X GET http://localhost:8091/products/main-categories/1?page\=0\&size\=10 -H 'Accept: application/json' | jq

GET products by name matches for users

    curl -v -X GET http://localhost:8091/products/names/search\?productName\=s\&page\=0\&size\=10 -H 'Accept: application/json' | jq

GET product info for generate invoices

     curl -v -X GET http://localhost:8091/products/invoices\?productBarCode\=8812329632587\&productName\=Samsung+Galaxy+S9,+64GB,+Midnight+Black -H 'Accept: application/json' | jq

GET get a ProductResponse DTO through barcode or name for employees, categories will only show the name to avoid unnecessary information

     curl -v -X GET http://localhost:8091/products/responses\?productBarCode\=5812329632587&productName=Adobe+Photoshop+Elements+2020 -H 'Accept: application/json' | jq

GET ProductListView that have at least one of the specified subcategories

     curl -v -X GET http://localhost:8091/products/sub-categories \
       -H 'Content-Type: application/json' \
       --data-raw '[
             "Web Cameras",
             "Monitors",
             "Mice"
       ]' | jq

POST, create a new product record, the main category and the sub categories must already be registered

    curl -v -X POST http://localhost:8091/products \
      -H 'Content-Type: application/json' \
      --data-raw '{
        "productBarCode": 1212345678912,
          "productName": "Iphone 11",
          "productDescription": "Mobile Phone , Operating System: MacOs.",
          "productStock": 16,
          "productPrice": 0.0,
          "mainCategoryName": "Electronics",
          "subCategoriesNames": ["Smartphones","Smart Divices"]
      }' | jq

PUT, increase or decrease(with negative quantity) the stock of a product using the barcode

     curl -v -X PUT http://localhost:8091/products/6012329632587/stock\?quantity\=100

PUT, update a product, the main category and the sub categories must already be registered

    curl -v -X PUT http://localhost:8091/products/3 \
      -H 'Content-Type: application/json' \
      --data-raw '{
        "productBarCode": 1212345678911,
          "productName": "Iphone 100",
          "productDescription": "Mobile Phone , Operating System: MacOs.",
          "productStock": 150,
          "productPrice": 850.99,
          "mainCategoryName": "Electronics",
          "subCategoriesNames": ["Smartphones","Smart Divices"]
      }' | jq

DELETE a product using the ID

    curl -v -X DELETE http://localhost:8091/products/11

### Main Categories

GET a page of main category names

    curl -v -X GET http://localhost:8091/main-categories\?page\=0\&size\=5 -H 'Accept: application/json' | jq

GET a main category by name

    curl -v -X GET http://localhost:8091/main-categories/Computers%20%26%20Accessories -H 'Accept: application/json' | jq

PUT update the name of a main category using the ID

    curl -v -X PUT http://localhost:8091/main-categories/2\?mainCategoryName\=Cell%20Phone -H 'Accept: application/json' | jq

POST creaate a main category

    curl -v -X POST http://localhost:8091/main-categories \
      -H 'Content-Type: application/json' \
      --data-raw '{
        "mainCategoryName": "Systems"
      }' | jq

DELETE a main category using the ID

     curl -v -X DELETE http://localhost:8091/main-categories/2

### Sub Categories

GET a page of sub category names

    curl -v -X GET http://localhost:8091/sub-categories/?page=0&size=5 -H 'Accept: application/json' | jq

GET get a SubCategoryResponse DTO through name for employees, category and product will only show the name to avoid unnecessary information

     curl -v -X GET http://localhost:8091/sub-categories/Mice -H 'Accept: application/json' | jq

PUT update the name of a sub category through ID

    curl -v -X PUT http://localhost:8091/sub-categories/1\?subCategoryName\=Rats -H 'Accept: application/json' | jq

POST create a sub category

     curl -v -X POST http://localhost:8091/sub-categories \
       -H 'Content-Type: application/json' \
       --data-raw '{
         "subCategoryName": "Android OS",
           "mainCategoryName": "Software"
       }' | jq

DELETE a sub category through ID

    curl -v -X DELETE http://localhost:8091/sub-categories/13
