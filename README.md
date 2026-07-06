Option 1: Delete the old schema (development only)

If you're still developing and have no production data, you can remove the existing subject from the Schema Registry.

```
  If you're still developing and have no production data, you can remove the existing subject from the Schema Registry.
```

If permanent deletion is enabled:

```
  curl -X DELETE "http://localhost:8081/subjects/inventory-allocated-topic-value?permanent=true"
```
