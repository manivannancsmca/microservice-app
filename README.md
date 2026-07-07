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
|----------------------------------|---------------------------------------------|

---

### Commands you'll use most in Kibana Dev Tools

# List all indices
GET _cat/indices?v

# View all documents
GET products/_search

# View first 10 documents
GET products/_search
{
  "size": 10
}

# Count documents
GET products/_count

# View mapping
GET products/_mapping

# Get a document by ID
GET products/_doc/1

# Delete an index
DELETE products

