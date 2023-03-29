Pub/Sub Proto to BigQuery Template
---
A streaming pipeline that reads Protobuf messages from a Pub/Sub subscription and writes them to a BigQuery table.

:memo: This is a Google-provided template! Please
check [Provided templates documentation](https://cloud.google.com/dataflow/docs/guides/templates/provided/pubsub-proto-to-bigquery)
on how to use it without having to build from sources.

:bulb: This is a generated documentation based
on [Metadata Annotations](https://github.com/GoogleCloudPlatform/DataflowTemplates#metadata-annotations)
. Do not change this file directly.

## Parameters

### Mandatory Parameters

* **protoSchemaPath** (Cloud Storage Path to the Proto Schema File): Cloud Storage path to a self-contained descriptor set file. Example: gs://MyBucket/schema.pb. `schema.pb` can be generated by adding `--descriptor_set_out=schema.pb` to the `protoc` command that compiles the protos. The `--include_imports` flag can be used to guarantee that the file is self-contained.
* **fullMessageName** (Full Proto Message Name): The full message name (example: package.name.MessageName). If the message is nested inside of another message, then include all messages with the '.' delimiter (example: package.name.OuterMessage.InnerMessage). 'package.name' should be from the `package` statement, not the `java_package` statement.
* **inputSubscription** (Pub/Sub input subscription): Pub/Sub subscription to read the input from, in the format of 'projects/your-project-id/subscriptions/your-subscription-name' (Example: projects/your-project-id/subscriptions/your-subscription-name).
* **outputTableSpec** (BigQuery output table): BigQuery table location to write the output to. The name should be in the format <project>:<dataset>.<table_name>. The table's schema must match input objects.
* **outputTopic** (Output Pub/Sub topic): The name of the topic to which data should published, in the format of 'projects/your-project-id/topics/your-topic-name' (Example: projects/your-project-id/topics/your-topic-name).

### Optional Parameters

* **preserveProtoFieldNames** (Preserve Proto Field Names): Flag to control whether proto field names should be kept or converted to lowerCamelCase. If the table already exists, this should be based on what matches the table's schema. Otherwise, it will determine the column names of the created table. True to preserve proto snake_case. False will convert fields to lowerCamelCase. (Default: false).
* **bigQueryTableSchemaPath** (BigQuery Table Schema Path): Cloud Storage path to the BigQuery schema JSON file. If this is not set, then the schema is inferred from the Proto schema. (Example: gs://MyBucket/bq_schema.json).
* **udfOutputTopic** (Pub/Sub output topic for UDF failures): An optional output topic to send UDF failures to. If this option is not set, then failures will be written to the same topic as the BigQuery failures. (Example: projects/your-project-id/topics/your-topic-name).
* **writeDisposition** (Write Disposition to use for BigQuery): BigQuery WriteDisposition. For example, WRITE_APPEND, WRITE_EMPTY or WRITE_TRUNCATE. Defaults to: WRITE_APPEND.
* **createDisposition** (Create Disposition to use for BigQuery): BigQuery CreateDisposition. For example, CREATE_IF_NEEDED, CREATE_NEVER. Defaults to: CREATE_IF_NEEDED.
* **javascriptTextTransformGcsPath** (Cloud Storage path to Javascript UDF source): The Cloud Storage path pattern for the JavaScript code containing your user-defined functions. (Example: gs://your-bucket/your-function.js).
* **javascriptTextTransformFunctionName** (UDF Javascript Function Name): The name of the function to call from your JavaScript file. Use only letters, digits, and underscores. (Example: 'transform' or 'transform_udf1').
* **useStorageWriteApi** (Use BigQuery Storage Write API): If enabled (set to true) the pipeline will use Storage Write API when writing the data to BigQuery (see https://cloud.google.com/blog/products/data-analytics/streaming-data-into-bigquery-using-storage-write-api). If this is enabled and at-least-once semantics (useStorageWriteApiAtLeastOnce) option is off then "Number of streams for BigQuery Storage Write API" and "Triggering frequency in seconds for BigQuery Storage Write API" must be provided. Defaults to: false.
* **useStorageWriteApiAtLeastOnce** (Use at at-least-once semantics in BigQuery Storage Write API): This parameter takes effect only if "Use BigQuery Storage Write API" is enabled. If enabled the at-least-once semantics will be used for Storage Write API, otherwise exactly-once semantics will be used. Defaults to: false.
* **numStorageWriteApiStreams** (Number of streams for BigQuery Storage Write API): Number of streams defines the parallelism of the BigQueryIO’s Write transform and roughly corresponds to the number of Storage Write API’s streams which will be used by the pipeline. See https://cloud.google.com/blog/products/data-analytics/streaming-data-into-bigquery-using-storage-write-api for the recommended values. Defaults to: 0.
* **storageWriteApiTriggeringFrequencySec** (Triggering frequency in seconds for BigQuery Storage Write API): Triggering frequency will determine how soon the data will be visible for querying in BigQuery. See https://cloud.google.com/blog/products/data-analytics/streaming-data-into-bigquery-using-storage-write-api for the recommended values.

## Getting Started

### Requirements

* Java 11
* Maven
* Valid resources for mandatory parameters.
* [gcloud CLI](https://cloud.google.com/sdk/gcloud), and execution of the
  following commands:
    * `gcloud auth login`
    * `gcloud auth application-default login`

The following instructions use the
[Templates Plugin](https://github.com/GoogleCloudPlatform/DataflowTemplates#templates-plugin)
. Install the plugin with the following command to proceed:

```shell
mvn clean install -pl plugins/templates-maven-plugin -am
```

### Building Template

This template is a Flex Template, meaning that the pipeline code will be
containerized and the container will be executed on Dataflow. Please
check [Use Flex Templates](https://cloud.google.com/dataflow/docs/guides/templates/using-flex-templates)
and [Configure Flex Templates](https://cloud.google.com/dataflow/docs/guides/templates/configuring-flex-templates)
for more information.

#### Staging the Template

If the plan is to just stage the template (i.e., make it available to use) by
the `gcloud` command or Dataflow "Create job from template" UI,
the `-PtemplatesStage` profile should be used:

```shell
export PROJECT=<my-project>
export BUCKET_NAME=<bucket-name>

mvn clean package -PtemplatesStage  \
-DskipTests \
-DprojectId="$PROJECT" \
-DbucketName="$BUCKET_NAME" \
-DstagePrefix="templates" \
-DtemplateName="PubSub_Proto_to_BigQuery" \
-pl v2/pubsub-binary-to-bigquery \
-am
```

The command should build and save the template to Google Cloud, and then print
the complete location on Cloud Storage:

```
Flex Template was staged! gs://<bucket-name>/templates/flex/PubSub_Proto_to_BigQuery
```

The specific path should be copied as it will be used in the following steps.

#### Running the Template

**Using the staged template**:

You can use the path above run the template (or share with others for execution).

To start a job with that template at any time using `gcloud`, you can use:

```shell
export PROJECT=<my-project>
export BUCKET_NAME=<bucket-name>
export REGION=us-central1
export TEMPLATE_SPEC_GCSPATH="gs://$BUCKET_NAME/templates/flex/PubSub_Proto_to_BigQuery"

### Mandatory
export PROTO_SCHEMA_PATH=<protoSchemaPath>
export FULL_MESSAGE_NAME=<fullMessageName>
export INPUT_SUBSCRIPTION=<inputSubscription>
export OUTPUT_TABLE_SPEC=<outputTableSpec>
export OUTPUT_TOPIC=<outputTopic>

### Optional
export PRESERVE_PROTO_FIELD_NAMES=false
export BIG_QUERY_TABLE_SCHEMA_PATH=<bigQueryTableSchemaPath>
export UDF_OUTPUT_TOPIC=<udfOutputTopic>
export WRITE_DISPOSITION="WRITE_APPEND"
export CREATE_DISPOSITION="CREATE_IF_NEEDED"
export JAVASCRIPT_TEXT_TRANSFORM_GCS_PATH=<javascriptTextTransformGcsPath>
export JAVASCRIPT_TEXT_TRANSFORM_FUNCTION_NAME=<javascriptTextTransformFunctionName>
export USE_STORAGE_WRITE_API=false
export USE_STORAGE_WRITE_API_AT_LEAST_ONCE=false
export NUM_STORAGE_WRITE_API_STREAMS=0
export STORAGE_WRITE_API_TRIGGERING_FREQUENCY_SEC=<storageWriteApiTriggeringFrequencySec>

gcloud dataflow flex-template run "pubsub-proto-to-bigquery-job" \
  --project "$PROJECT" \
  --region "$REGION" \
  --template-file-gcs-location "$TEMPLATE_SPEC_GCSPATH" \
  --parameters "protoSchemaPath=$PROTO_SCHEMA_PATH" \
  --parameters "fullMessageName=$FULL_MESSAGE_NAME" \
  --parameters "preserveProtoFieldNames=$PRESERVE_PROTO_FIELD_NAMES" \
  --parameters "bigQueryTableSchemaPath=$BIG_QUERY_TABLE_SCHEMA_PATH" \
  --parameters "udfOutputTopic=$UDF_OUTPUT_TOPIC" \
  --parameters "inputSubscription=$INPUT_SUBSCRIPTION" \
  --parameters "outputTableSpec=$OUTPUT_TABLE_SPEC" \
  --parameters "writeDisposition=$WRITE_DISPOSITION" \
  --parameters "createDisposition=$CREATE_DISPOSITION" \
  --parameters "outputTopic=$OUTPUT_TOPIC" \
  --parameters "javascriptTextTransformGcsPath=$JAVASCRIPT_TEXT_TRANSFORM_GCS_PATH" \
  --parameters "javascriptTextTransformFunctionName=$JAVASCRIPT_TEXT_TRANSFORM_FUNCTION_NAME" \
  --parameters "useStorageWriteApi=$USE_STORAGE_WRITE_API" \
  --parameters "useStorageWriteApiAtLeastOnce=$USE_STORAGE_WRITE_API_AT_LEAST_ONCE" \
  --parameters "numStorageWriteApiStreams=$NUM_STORAGE_WRITE_API_STREAMS" \
  --parameters "storageWriteApiTriggeringFrequencySec=$STORAGE_WRITE_API_TRIGGERING_FREQUENCY_SEC"
```

For more information about the command, please check:
https://cloud.google.com/sdk/gcloud/reference/dataflow/flex-template/run


**Using the plugin**:

Instead of just generating the template in the folder, it is possible to stage
and run the template in a single command. This may be useful for testing when
changing the templates.

```shell
export PROJECT=<my-project>
export BUCKET_NAME=<bucket-name>
export REGION=us-central1

### Mandatory
export PROTO_SCHEMA_PATH=<protoSchemaPath>
export FULL_MESSAGE_NAME=<fullMessageName>
export INPUT_SUBSCRIPTION=<inputSubscription>
export OUTPUT_TABLE_SPEC=<outputTableSpec>
export OUTPUT_TOPIC=<outputTopic>

### Optional
export PRESERVE_PROTO_FIELD_NAMES=false
export BIG_QUERY_TABLE_SCHEMA_PATH=<bigQueryTableSchemaPath>
export UDF_OUTPUT_TOPIC=<udfOutputTopic>
export WRITE_DISPOSITION="WRITE_APPEND"
export CREATE_DISPOSITION="CREATE_IF_NEEDED"
export JAVASCRIPT_TEXT_TRANSFORM_GCS_PATH=<javascriptTextTransformGcsPath>
export JAVASCRIPT_TEXT_TRANSFORM_FUNCTION_NAME=<javascriptTextTransformFunctionName>
export USE_STORAGE_WRITE_API=false
export USE_STORAGE_WRITE_API_AT_LEAST_ONCE=false
export NUM_STORAGE_WRITE_API_STREAMS=0
export STORAGE_WRITE_API_TRIGGERING_FREQUENCY_SEC=<storageWriteApiTriggeringFrequencySec>

mvn clean package -PtemplatesRun \
-DskipTests \
-DprojectId="$PROJECT" \
-DbucketName="$BUCKET_NAME" \
-Dregion="$REGION" \
-DjobName="pubsub-proto-to-bigquery-job" \
-DtemplateName="PubSub_Proto_to_BigQuery" \
-Dparameters="protoSchemaPath=$PROTO_SCHEMA_PATH,fullMessageName=$FULL_MESSAGE_NAME,preserveProtoFieldNames=$PRESERVE_PROTO_FIELD_NAMES,bigQueryTableSchemaPath=$BIG_QUERY_TABLE_SCHEMA_PATH,udfOutputTopic=$UDF_OUTPUT_TOPIC,inputSubscription=$INPUT_SUBSCRIPTION,outputTableSpec=$OUTPUT_TABLE_SPEC,writeDisposition=$WRITE_DISPOSITION,createDisposition=$CREATE_DISPOSITION,outputTopic=$OUTPUT_TOPIC,javascriptTextTransformGcsPath=$JAVASCRIPT_TEXT_TRANSFORM_GCS_PATH,javascriptTextTransformFunctionName=$JAVASCRIPT_TEXT_TRANSFORM_FUNCTION_NAME,useStorageWriteApi=$USE_STORAGE_WRITE_API,useStorageWriteApiAtLeastOnce=$USE_STORAGE_WRITE_API_AT_LEAST_ONCE,numStorageWriteApiStreams=$NUM_STORAGE_WRITE_API_STREAMS,storageWriteApiTriggeringFrequencySec=$STORAGE_WRITE_API_TRIGGERING_FREQUENCY_SEC" \
-pl v2/pubsub-binary-to-bigquery \
-am
```