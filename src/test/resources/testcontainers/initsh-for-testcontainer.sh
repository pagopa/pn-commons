echo "### CREATE PN-COMMONS TABLE ###"

TABLE_NAME_FIRST="FirstTable"

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name "$TABLE_NAME_FIRST" \
    --attribute-definitions \
        AttributeName=firstId,AttributeType=S \
        AttributeName=description,AttributeType=S \
        AttributeName=name,AttributeType=S \
        AttributeName=code,AttributeType=S \
    --key-schema \
        AttributeName=firstId,KeyType=HASH \
        AttributeName=description,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5 \
    --global-secondary-index \
        "[
            {
                \"IndexName\": \"index-first\",
                \"KeySchema\": [{\"AttributeName\":\"name\",\"KeyType\":\"HASH\"},{\"AttributeName\":\"code\",\"KeyType\":\"RANGE\"}],
                \"Projection\":{
                    \"ProjectionType\":\"ALL\"
                },
                \"ProvisionedThroughput\": {
                    \"ReadCapacityUnits\": 5,
                    \"WriteCapacityUnits\": 5
                }
            }
    	]"

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name SecondTable \
    --attribute-definitions \
        AttributeName=secondId,AttributeType=S \
		    AttributeName=index,AttributeType=S \
		    AttributeName=value,AttributeType=S \
    --key-schema \
        AttributeName=secondId,KeyType=HASH \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5 \
    --global-secondary-index \
    "[
        {
            \"IndexName\": \"index\",
            \"KeySchema\": [{\"AttributeName\":\"index\",\"KeyType\":\"HASH\"},{\"AttributeName\":\"value\",\"KeyType\":\"RANGE\"}],
            \"Projection\":{
                \"ProjectionType\":\"ALL\"
            },
            \"ProvisionedThroughput\": {
                \"ReadCapacityUnits\": 5,
                \"WriteCapacityUnits\": 5
            }
        }
	]"


TABLE_NAME="FirstTable"

# array of firstTable entity
data=(
    '{"firstId": {"S": "id1"}, "name": {"S": "Name 1"}, "description": {"S": "Descrizione 1"}, "price": {"N": "10.99"}, "code": {"S": "Code 1"}}'
    '{"firstId": {"S": "id2"}, "name": {"S": "Name 1"}, "description": {"S": "Descrizione 2"}, "price": {"N": "20.50"}, "code": {"S": "Code 2"}}'
    '{"firstId": {"S": "id3"}, "name": {"S": "Name 1"}, "description": {"S": "Descrizione 3"}, "price": {"N": "30.75"}, "code": {"S": "Code 3"}}'
    '{"firstId": {"S": "id4"}, "name": {"S": "Name 1"}, "description": {"S": "Descrizione 4"}, "price": {"N": "40.00"}, "code": {"S": "Code 4"}}'
    '{"firstId": {"S": "id5"}, "name": {"S": "Name 1"}, "description": {"S": "Descrizione 5"}, "price": {"N": "50.25"}, "code": {"S": "Code 5"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 1"}, "description": {"S": "Descrizione 1"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 1"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 2"}, "description": {"S": "Descrizione 2"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 2"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 3"}, "description": {"S": "Descrizione 3"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 3"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 4"}, "description": {"S": "Descrizione 4"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 4"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 5"}, "description": {"S": "Descrizione 5"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 5"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 6"}, "description": {"S": "Descrizione 6"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 6"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 7"}, "description": {"S": "Descrizione 7"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 7"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 8"}, "description": {"S": "Descrizione 8"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 8"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 9"}, "description": {"S": "Descrizione 9"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 9"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 10"}, "description": {"S": "Descrizione 10"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 10"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 11"}, "description": {"S": "Descrizione 11"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 11"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 12"}, "description": {"S": "Descrizione 12"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 12"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 13"}, "description": {"S": "Descrizione 13"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 13"}}'
    '{"firstId": {"S": "idDelete"}, "name": {"S": "Delete Name 14"}, "description": {"S": "Descrizione 14"}, "price": {"N": "50.25"}, "code": {"S": "Delete Code 14"}}'
)

# Loop for put item into DynamoDB
for item in "${data[@]}"; do
    aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
     dynamodb put-item \
    --table-name "$TABLE_NAME" \
    --item "$item"
done


echo "Initialization terminated"