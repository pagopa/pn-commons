echo "### CREATE PN-COMMONS TABLE ###"

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name FirstTable \
    --attribute-definitions \
        AttributeName=firstId,AttributeType=S \
        AttributeName=description,AttributeType=S \
    --key-schema \
        AttributeName=firstId,KeyType=HASH \
        AttributeName=description,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5

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

echo "Initialization terminated"