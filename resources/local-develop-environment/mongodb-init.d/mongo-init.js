// admin 데이터베이스에 연결
db = connect("mongodb://root:root@localhost:27017/admin");

//  데이터베이스 생성
db = db.getSiblingDB('crm');

// 사용자 생성
db.createUser({
    user: "crm",
    pwd: "crm",
    roles: [
        {
            role: "readWrite",
            db: "crm"
        }
    ]
});
