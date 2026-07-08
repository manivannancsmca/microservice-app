Option 1: Delete the old schema (development only)

If you're still developing and have no production data, you can remove the existing subject from the Schema Registry.

```
curl -X DELETE http://localhost:8081/subjects/inventory-allocated-topic-value
```

If permanent deletion is enabled:

```
curl -X DELETE "http://localhost:8081/subjects/inventory-allocated-topic-value?permanent=true"
```

### MySQL vs Elasticsearch Cheat Sheet

---

| MySQL                            | Elasticsearch                               |
| -------------------------------- | ------------------------------------------- |
| `SHOW TABLES;`                   | `GET _cat/indices?v`                        |
| `SELECT * FROM products;`        | `GET products/_search`                      |
| `SELECT COUNT(*) FROM products;` | `GET products/_count`                       |
| `DESCRIBE products;`             | `GET products/_mapping`                     |
| `SELECT * WHERE id=1;`           | `GET products/_doc/1`                       |
| `SELECT * WHERE name='iphone';`  | `GET products/_search` with a `match` query |
| `DROP TABLE products;`           | `DELETE products`                           |
| `CREATE TABLE products...`       | `PUT products`                              |

---

### Commands you'll use most in Kibana Dev Tools

1. List all indices
GET _cat/indices?v

2. View all documents
GET products/_search

3. View first 10 documents
GET products/_search
{
  "size": 10
}

4. Count documents
GET products/_count

5. View mapping
GET products/_mapping

6. Get a document by ID
GET products/_doc/1

7. Delete an index
DELETE products

Since your index is products, you can insert documents directly from Kibana Dev Tools.

# Elasticsearch Products Index Setup Guide

This guide explains how to insert and search documents in the `products` index using **Kibana Dev Tools**.

## Inserting Documents

Since your index is `products`, you can insert documents directly from Kibana Dev Tools.

### Step 1: Open Kibana Dev Tools

Go to:

**Kibana → Dev Tools**

### Step 2: Create a Document

Execute the following request:

```http
POST products/_doc
{
  "id": "1",
  "name": "Apple iPhone 16",
  "description": "Latest Apple smartphone with AI features",
  "price": 89999.99,
  "skuCode": "IPH16-001",
  "createdAt": "2026-07-08T12:00:00Z",
  "updatedAt": "2026-07-08T12:00:00Z"
}
```

**Response:**

```json
{
  "_index": "products",
  "_id": "abc123xyz...",
  "result": "created"
}
```

### Step 3: Specify Your Own Elasticsearch Document ID (Recommended)

Notice that your Java class has:

```java
@Id
private String id;
```

This does **not** automatically become the Elasticsearch `_id` when inserting via Kibana. It is simply another field inside the document.

To set both `_id` and the `id` field to the same value, use:

```http
PUT products/_doc/1
{
  "id": "1",
  "name": "Apple iPhone 16",
  "description": "Latest Apple smartphone with AI features",
  "price": 89999.99,
  "skuCode": "IPH16-001",
  "createdAt": "2026-07-08T12:00:00Z",
  "updatedAt": "2026-07-08T12:00:00Z"
}
```

Now `_id = 1` and `id = 1`.

### Step 4: Verify the Document

```http
GET products/_search
```

or

```http
GET products/_search
{
  "query": {
    "match_all": {}
  }
}
```

**Example Output:**

```json
{
  "hits": {
    "hits": [
      {
        "_id": "1",
        "_source": {
          "id": "1",
          "name": "Apple iPhone 16",
          "description": "Latest Apple smartphone with AI features",
          "price": 89999.99,
          "skuCode": "IPH16-001",
          "createdAt": "2026-07-08T12:00:00Z",
          "updatedAt": "2026-07-08T12:00:00Z"
        }
      }
    ]
  }
}
```

---

## Insert Multiple Documents (Bulk API)

You can insert multiple documents efficiently using the Bulk API:

```http
POST products/_bulk
{ "index": { "_id": "1" } }
{ "id":"1","name":"iPhone 16","description":"Apple Phone","price":89999,"skuCode":"IPH001","createdAt":"2026-07-08T12:00:00Z","updatedAt":"2026-07-08T12:00:00Z" }
{ "index": { "_id": "2" } }
{ "id":"2","name":"Samsung S26","description":"Samsung Flagship","price":79999,"skuCode":"SAM001","createdAt":"2026-07-08T12:00:00Z","updatedAt":"2026-07-08T12:00:00Z" }
{ "index": { "_id": "3" } }
{ "id":"3","name":"OnePlus 14","description":"Fast Android Phone","price":59999,"skuCode":"ONE001","createdAt":"2026-07-08T12:00:00Z","updatedAt":"2026-07-08T12:00:00Z" }
```

---

## Search Examples

### Search by Name

```http
GET products/_search
{
  "query": {
    "match": {
      "name": "iphone"
    }
  }
}
```

### Search by SKU Code

Since `skuCode` is a **Keyword** field:

```http
GET products/_search
{
  "query": {
    "term": {
      "skuCode": "IPH001"
    }
  }
}
```

### Search by Price Range

```http
GET products/_search
{
  "query": {
    "range": {
      "price": {
        "gte": 50000,
        "lte": 90000
      }
    }
  }
}
```

---

## Important: Check Index Mapping

If the `products` index was created automatically before your Spring Data Elasticsearch mappings were applied, Elasticsearch may have inferred incorrect field types.

Check the current mapping:

```http
GET products/_mapping
```

**Recommendation**: If the mapping doesn't match your `ProductDocument` annotations, delete the index and let your Spring Boot application recreate it with the correct mapping.

---

**Happy Indexing!** 🚀
