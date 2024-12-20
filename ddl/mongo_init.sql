db.getSiblingDB("fish").createUser(
    {
        user: "admin",
        pwd: "123456",
        roles: [{ "role": "readWrite", "db": "fish" }],
    }
)