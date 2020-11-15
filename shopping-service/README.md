# Shopping-Service

Microservice in charge of sales management.

# Execution Commands

TEST spring and maven profile

    mvn spring-boot:run -Dspring-boot.run.profiles=test -P test

POSTGRESQL spring and maven profile

    mvn spring-boot:run -Dspring-boot.run.profiles=postgresql -P postgresql


# Endpoints

## Invoices

POST, create a new invoice, this functionality is what communicates with product-service to obtain the name and price of the products

    curl -v -X POST http://localhost:8092/invoices \
      -H 'Content-Type: application/json' \
      --data-raw '{
                    "invoiceNumber" : 123456,
                    "products" : [
                      {
                        "productBarCode" :  6012329632587,
                        "productName": "Samsung Gear S3 Frontier Smartwatch (Bluetooth), SM-R760NDAAXAR",
                        "quantity": 2
                      },
                      {
                        "productBarCode" :  1232584561287,
                        "productName": "Logitech G203 Prodigy RGB Wired Gaming Mouse – Black",
                        "quantity": 3
                      }
                    ],
                    "customer": {
                      "userName": "test2",
                      "userEmail": "email2"
                   }
                  }' | jq

DELETE a invoice using the ID

    curl -v -X DELETE http://localhost:8092/invoices/1

GET invoices by user name

    curl -v -X GET http://localhost:8092/invoices/users?userName=test2&page=0&size=10 -H 'Accept: application/json' | jq

GET invoices by product barcode

    curl -v -X GET http://localhost:8092/invoices/products/6012329632587?page=0&size=10 -H 'Accept: application/json' | jq

GET invoices by invoice number

    curl -v -X GET http://localhost:8092/invoices/456023 -H 'Accept: application/json' | jq
