/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bigquery;

// [START bigquery_query_external_gcs_perm]
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.CsvOptions;
import com.google.cloud.bigquery.ExternalTableDefinition;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;

// Sample to queries an external data source using a permanent table
public class QueryExternalGcsPerm {

  public static void main(String[] args) {
    // TODO(developer): Replace these variables before running the sample.
    String datasetName = "MY_DATASET_NAME";
    String tableName = "MY_TABLE_NAME";
    String sourceUri = "gs://cloud-samples-data/bigquery/us-states/us-states.csv";
    Schema schema =
        Schema.of(
            Field.of("name", StandardSQLTypeName.STRING),
            Field.of("post_abbr", StandardSQLTypeName.STRING));
    String query =
        String.format("SELECT * FROM %s.%s WHERE name LIKE 'W%%'", datasetName, tableName);
    queryExternalGcsPerm(datasetName, tableName, sourceUri, schema, query);
  }

  public static void queryExternalGcsPerm(
      String datasetName, String tableName, String sourceUri, Schema schema, String query) {
    try {
      // Initialize client that will be used to send requests. This client only needs to be created
      // once, and can be reused for multiple requests.
      BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

      // Skip header row in the file.
      CsvOptions csvOptions = CsvOptions.newBuilder().setSkipLeadingRows(1).build();

      TableId tableId = TableId.of(datasetName, tableName);
      // Create a permanent table linked to the GCS file
      ExternalTableDefinition externalTable =
          ExternalTableDefinition.newBuilder(sourceUri, csvOptions).setSchema(schema).build();
      bigquery.create(TableInfo.of(tableId, externalTable));

      // Example query to find states starting with 'W'
      TableResult results = bigquery.query(QueryJobConfiguration.of(query));

      results
          .iterateAll()
          .forEach(row -> row.forEach(val -> System.out.printf("%s,", val.toString())));

      System.out.println("Query on external permanent table performed successfully.");
    } catch (BigQueryException | InterruptedException e) {
      System.out.println("Query not performed \n" + e.toString());
    }
  }
}
// [END bigquery_query_external_gcs_perm]
