# 몽고 DB 완벽 가이드

- [CHAPTER 1 몽고DB 소개](#chapter-1-몽고db-소개)
- [CHAPTER 2 몽고DB 기본](#chapter-2-몽고db-기본)
- [CHAPTER 3 도큐먼트 생성, 갱신, 삭제](#chapter-3-도큐먼트-생성-갱신-삭제)
- [CHAPTER 4 쿼리](#chapter-4-쿼리)
- [CHAPTER 5 인덱싱](#chapter-5-인덱싱)
- [CHAPTER 6 특수 인덱스와 컬렉션 유형](#chapter-6-특수-인덱스와-컬렉션-유형)
- [CHAPTER 7 집계 프레임워크](#chapter-7-집계-프레임워크)
- [CHAPTER 8 트랜잭션](#chapter-8-트랜잭션)
- [CHAPTER 9 애플리케이션 설계](#chapter-9-애플리케이션-설계)
- [CHAPTER 10 복제 셋 설정](#chapter-10-복제-셋-설정)
- [CHAPTER 11 복제 셋 구성 요소](#chapter-11-복제-셋-구성-요소)
- [CHAPTER 12 애플리케이션에서 복제 셋 연결](#chapter-12-애플리케이션에서-복제-셋-연결)
- [CHAPTER 13 관리](#chapter-13-관리)
- [CHAPTER 14 샤딩 소개](#chapter-14-샤딩-소개)
- [CHAPTER 15 샤딩 구성](#chapter-15-샤딩-구성)
- [CHAPTER 16 샤드 키 선정](#chapter-16-샤드-키-선정)
- [CHAPTER 17 샤딩 관리](#chapter-17-샤딩-관리)
- [CHAPTER 18 애플리케이션 작업 확인](#chapter-18-애플리케이션-작업-확인)
- [CHAPTER 19 몽고DB 보안 소개](#chapter-19-몽고db-보안-소개)
- [CHAPTER 20 영속성](#chapter-20-영속성)
- [CHAPTER 21 몽고DB 시작과 중지](#chapter-21-몽고db-시작과-중지)
- [CHAPTER 22 몽고DB 모니터링](#chapter-22-몽고db-모니터링)
- [CHAPTER 23 백업](#chapter-23-백업)
- [CHAPTER 24 몽고DB 배포](#chapter-24-몽고db-배포)



# 몽고 DB 완벽가이드

# 1. 몽고 DB 시작

몽고 DB는 분산 확장을 염두에 두고 설계됐따.

<img src="./images/Untitled.png"/>

도큐먼트를 자동으로 재분배하고 사용자의 요청을 올바른 장비에 라우팅한다.

# 2. 몽고 DB 기본

기본 단위 : 

- 도큐먼트 = RDB ROW
- 컬렉션 : RDB TABLE
- 모든 도큐먼트는 고유값인 id를 가진다.

몽고 DB는 key value 모두  대소문자 키값을 구분한다.

key는 중복될 수 없다.

스키마는 동적이라, 하나의 컬렉션(테이블)내의 도큐먼트(row)들이 모두 다른 구조를 가질 수 있다. 

도큐먼트는 다 다른구조를 가질 수 있는데 왜 별도의 컬렉션이 필요한가?

- 같은 컬렉션에 다른 종류의 도큐먼트를 저장하면 개발자와 관리자에게 번거로울 수 있다.
- 컬렉션은 관련 도큐먼트를 함께 그룹화하는 방법을 제공합니다. 예를 들어, '사용자' 컬렉션에는 사용자 프로필 도큐먼트가 포함될 수 있으며, '주문' 컬렉션에는 주문 정보 도큐먼트가 포함될 수 있습니다. 이런 식으로 데이터를 조직하면 데이터 관리가 더욱 효율적이고 직관적이 됩니다.
- **쿼리 성능**: 컬렉션별로 인덱스를 생성할 수 있어, 데이터 조회 시 성능을 향상시킬 수 있다.
- 같은 종류의 데이터를 컬렉션에 모아두면 data locality에도 좋다.

### 서브컬렉션

예를들어 블로그 기능이 있는 애플리케이션은 

blog.posts와 blog.authors라는 컬렉션을 가질 수 있다.

- 단지 체계화일 뿐이며 blog 컬렉션이나 자식 컬렉션이랑은 아무런 관계가 없다.

## 데이터베이스

데이터베이스에 컬렉션을 그룹 지을 수 있다.

단일 몽고DB 인스턴스는 여러 데이터베이스를 호스팅 할 수 있으며, 독립적으로 취급할 수 있다. 

```jsx
version: '3.8'
services:
  mongodb:
    image: mongo
    container_name: mongodb
    ports:
      - "27017:27017"
    environment:
      - MONGO_INITDB_ROOT_USERNAME=root
      - MONGO_INITDB_ROOT_PASSWORD=root
    volumes:
      - ./data/db:/data/db
    networks:
      - network-mongo
    hostname: mongodb

  mongo-express:
    depends_on:
      - mongodb
    image: mongo-express
    container_name: mongo-express
    ports:
      - "27117:8081"
    environment:
      - PORT=8081
      - ME_CONFIG_MONGODB_PORT=27017
      - ME_CONFIG_MONGODB_SERVER=mongodb
      - ME_CONFIG_MONGODB_ADMINUSERNAME=root
      - ME_CONFIG_MONGODB_ADMINPASSWORD=root
      - ME_CONFIG_MONGODB_URL=mongodb://root:root@mongodb:27017/
    networks:
      - network-mongo
    hostname: mongo-express

networks:
  network-mongo:
    driver: bridge

```

접속

```jsx
mongosh   -u root   -p root
```

- 데이터베이스 이름은 대소문자를 구별한다
- 데이터베이스 이름은 최대 64바이트다

예약된 데이터베이스 목록

- admin : 인증, 권한부여 역할
- local : 단일서버에 대한 데이터를 저장. 레플리카 셋에서 local은 replication 프로세스에 사용된 데이터를 저장한다. local 자체는 복제되지 않는다.
- config : 샤딩된 몽고 db클러스터는 config 데이터베이스를 사용해 각 샤드의 정보를 저장한다.

### 셸 기본 작업

**생성**

insertOne 함수는 컬렉션에 도큐먼트를 추가한다.

```jsx
video> db.movies
video.movies
video> movie = {
...   "title" : "Star woars",
...   "director" : "George Lucas",
...   "year" : 1977
... }
{ title: 'Star woars', director: 'George Lucas', year: 1977 }
video> db.movies.insertOne(movie)
{
  acknowledged: true,
  insertedId: ObjectId('6687508c29bf721a38314a1e')
}
video>
```

**읽기**

find와 findOne은 컬렉션을 쿼리하는데 사용한다.

단일 도큐먼트는 findOne을 사용한다.

```jsx
db.movies.findOne()
```

쿼리 도큐먼트 형태로도 전달 가능하다. 쿼리에서 일치하는 도큐먼트로 결과를 제한한다. 

**갱신**

updateOne을 사용한다.

updateOne의 매개변수는 최소 두 개다. 

1. 수정할 도큐먼트 찾는 기준
2. 갱신 작업을 설명하는 도큐먼트

```jsx
video> db.movies.updateOne({title : "Star woars"}, {$set : {reviews: []}})
{
  acknowledged: true,
  insertedId: null,
  matchedCount: 1,
  modifiedCount: 1,
  upsertedCount: 0
}
video> db.movies.findOne()
{
  _id: ObjectId('6687508c29bf721a38314a1e'),
  title: 'Star woars',
  director: 'George Lucas',
  year: 1977,
  reviews: []
}
```

**삭제**

deleteOne과 deleteMany는 도큐먼트를 영구적으로 삭제한다 

```jsx
db.movies.deleteOne(
  {
    title : "Star woars"
  }
)
```

## 2.6 데이터형

JSON과 닮긴했다.

몽고는 JSON의 키/값 쌍 성질을 유지하면서 추가적인 데이터형을 지원한다.

- null
- boolean
- 숫자. 부동소수점 수도 포함
- 문자열 : 어떤 UTF-8문자열이든 표현가능
- 날짜. 1970년 1월 1일부터 시간을 1/1000초 단위로 나타내는 64비트 정수로 저장. 시간대는 저장하지 않는다.
- 정규 표현식
- 배열
- 내장 도큐먼트 (내장 오브젝트)
- 객체 id : ObjectId() 도큐먼트용 12바이트 ID

### 날짜

항상 날짜는 new Date()로 생성자로 생성해야 한다.

표준시간대 정보는 없으므로 다른 키값으로 저장해야 한다.

### 배열

배열은 서로 다른 데이터형을 가질 수 있다.

```jsx
{ "things" : ["pie", 3.14]}
```

중첩 배열도 가능하다. 

배열의 구조를 이해하기 때문에, 배열에 쿼리하거나 배열의 내용을 이용해 인덱스를 만들 수 있다.

### _id와 ObjectId

모든 도큐먼트를 _id 키를 가진다. 

_id 키값은 어떤 데이터형이여도 상관없지만 ObjectId가 기본이다. 

하나의 컬렉션에서 모든 도큐먼트는 고유한 _id값을 가진다.

두개의 컬렉션에서는 상관없다. 

ObjectId는 _id의 게본 데이터형으로, 가벼우면서 여러 장비에 걸쳐 전역적으로 고유하게 생성하기 쉽게 설계됐다. 자동 증가 이유는 몽고DB의 분산 특성 때문이다. 

샤딩된 환경에서 고유 식별자를 생성하는것이 중요하기 때문이다.

ObjectId는 12바이트를 사용하며, 24자리 16진수 문자열 표현이 가능하다. 

- 바이트당 2자리를 사용한다.
- 16진수 문자열 표현이지만, 실제로 문자열은 저장된 데이터의 두 배만큼 길다.

<img src="./images/Untitled 1.png"/>

- 첫 4바이트는 1970년 1월1일부터 시간을 1/1000초 단위로 저장하는 타임스탬프다.
- 다음 5바이트는 랜덤값이다.
- 최종3바이트는 서로 다른 시스템에서 충돌하는 ObjectId들을 생성하지 않도록 랜덤값으로 시작하는 카운터다.

앞 9바이트는 1초동안 여러 장비와 프로세스에 걸쳐 유일성을 보장한다.

마지막 3바이트는 단순히 증분하는 숫자로이다.

- 고유한 ObjectId는 프로세스당 1초에 256^3 (1677만 7216개)까지 생성된다.

## 몽고 DB 셸 사용

호스트명, 포트, 데이터베이스를 명시해야 한다 

```jsx
mongo localhost:300000/mydb

mongo --nodb // --nodb 옵션시 어디에도 연결되지 않음 
```

# 3. 도큐먼트 생성, 갱신, 삭제

## 3.1 도큐먼트 삽입

insertOne 메서드를 사용한다.

모든 도큐먼트에는 _id가 추가된다

insertMany()는 도큐먼트 배열을 받아 bulk Insert한다.

한번에 일괄삽입할 수 있는 데이터 크기는 48MB이며, 이보다 크면 분할해서 넣어야 한다.

## 도큐먼트 삭제

deleteOne과 deleteMany를 제공한다.

필터링(조건) 매개변수를 첫번째 매개변수로 사용한다.

전체 컬렉션을 사용하려면 drop()을 사용한다. db.movies.drop()

- 더 빠르다
- 삭제된 도큐먼트를 복구하는 방법은 없다.

### 도큐먼트 갱신

updateOne, updateMany, replaceOne을 지원한다.

갱신은 원자적으로 이뤄진다. 갱신 요청 두개가 동시에 발생하면, 먼저 도착한 요청이 적용된 후 다음 요청이 적용된다. 

따라서 여러 갱신 요청이 빠르게 발생해도, 마지막 요청이 최후의 승리자가 된다. 

이 동작을 원하지 앟으면 도큐먼트 버저닝 패턴을 고려할 수 있다.

replaceOne은 도큐먼트를 완전히 새로운것으로 치환한다. 대대적인 스키마 마이그레이션에 유용하다.

조건절로 2개 이상의 도큐먼트가 일치되고 갱신을 요청하게 되면 오류를 반환한다.

고유한 도큐먼트의 값으로 갱신을 시도하는것이 좋다.

### **갱신 연산자 (update operator)**

갱신 연산자는 키를 변경 추가 제거하고 배열과 내장 도큐먼트를 조작하는 연산을 지정하는데 사용되는 특수 키다.

누군가가 페이지를 방문할때마다 방문 수를 증가시키고 싶다면?

$inc modifer를 사용한다

```jsx
{
"id" : ObjectId("4b253b067525f35f94b60a31"),
"url": "www.example.com",
"pageviews" : 52
}

db.analytics.updateOne(
  { "url" : "www.example.com"},
  { "$inc" : {"pageviews" : 1}} 
)
```

_id 값은 절대 변경할 수 없다.

그외 다른 키 값은 변경할 수 있다.

$set은 필드값을 설정한다. 필드가 존재하지 않으면 새 필드가 생성된다.

```jsx
> db.users.findOne()
{
"_id" : ObjectId ("4b253b067525f35f94b60a31"),
"name" : "joe",
"age" : 30,
"sex" : "male",
"location" : "Wisconsin"
}

db.users.updateOne(
 {"_id" : ObjectId("4b253b067525f35f94b60a31")},
 {"$set" : {"favorite book" : "War and Peace}}
)
```

$set은 키의 데이터형도 변경할 수 있다. 배열로도 변경 가능한것이다.

$unset으로 키와 값을 모두 제거할 수 있다.

$Inc는 1~n까지 증가시킬 수 있으며, 숫자를 증감하기 위해 설계됐다.

int, long ,double, decimal 타입 값에만 사용 가능하다. 문자, 널, 불리언 등은 불가능하다 

### 배열 연산자 (array operator)

배열 연산자는 리스트에 대한 인덱스 지정 및 set처럼 이중으로 쓸수있다

요소 추가하기 - $push

$push는 배열이 이미 존재하면 배열 끝에 요소를 추가하고, 존재하지 않으면 새로울 배열을 생성한다.

$push에 $each 를 사용하면 작업 한번으로 여러 값을 추가할 수 있다.

```jsx
db.stock.ticker.updateOne(
{"_id" : "GOOG"},
{"$push" : {"hourly" : {"each" : [562.123, 562.1234]}}}
)
```

$slice는 길이를 늘릴수도있고, 특정 길이 이상으로 늘어나지 않도록 길이를 제한할 수 있다.

큐로도 만들수가 있다.

```jsx
db.movies.updateOne({"genre": "horror"},
{"$push" : {"top10" : {"$each" : ["Nightmare on Elm Street", "Saw"], 
           "$slice" : -10
}}}
)
```

- 예제는 배열에 추가할 수 있는 요소 수를 10개로 제한함.

계속 넣었을 때, 10보다 크면 마지막 10개 요소만 유지된다. 

$sort 제한자를 $push 작업에 적용할수있다.

```jsx
db.movies.updateOne({"genre": "horror"},
{"$push" : {
           "top10" : {"$each" : ["Nightmare on Elm Street", "Saw"], 
           "$slice" : -10,
           "$sort" : { "rating" : -1 }
}}}
)
```

### 배열을 집합으로 사용하기

특정 값이 배열에 존재하지않을때만 값을 추가하면서 집합처럼 사용할 수 있다.

$ne (non equal)을 이용할 수 있다.

```jsx
db.papers.updateOne({"authors cited" : {"$ne" : "Richie"}},
 { $push : {"authors cited" : "Richie"}}
)
```

"$addToSet"을 사용할 수도 있다. "saddToSet"은 "$ne"가 작동하지 않을 때나
"$addToSet"을 사용하면 무슨 일이 일어났는지 더 잘 알 수 있을 때 유용하다.

유니크 값을 여러개 추가하려면 $addToSet과 $each를 결합해 사용한다. 

### 요소 제거하기

몇가지 방법이 있다. 

배열을 큐나 스택처럼 사용하면서 요소 제거하는 경우에는 $poㅔ을 사용한다.

{”$pop” : {”key” : 1}}은 배열의 마지막부터 요소를 제거하고

{”$pop” : {”key” : -1}}은 배열의 처음부터 요소를 제거한다. 

**배열의 위치 기반 변경**

배열의 위치 변경 방법은 2가지가 있다.

인덱스를 이용하거나 $ 문자를 사용한다

```jsx
db.blog.updateOne({"post" : post_id},
{"$inc" : {"comments.0.votes" : 1}}
)
```

보통 도큐먼트를 쿼리해서 검색해보지 않고는 몇번 째 요소를 변경할 지 알 수 없다.

이문제를 해결하기 위해 몽고에서는 쿼리 도큐먼트와 일치하는 배열 요소 및 요소의 위치를 알아내서 갱싱하는 위치 연산자 $를 제공한다. 

예를들어 john이라는 사용자가 이름을 jim으로 갱신할 씨, 위치 연산자를 사용해서 댓글 내 해당 항목을 갱싱한다

```jsx
db.blog.updateOne({"comments.author" : "john"},
{"$set" : {"comments.$.author" : "jim"}}
)
```

- 위치 연산자는 첫 번째로 일치하는 요소만 갱신한다.
- john이 2개 이상 남겼다면, 처음 남긴 댓글의 작성자명만 변경된다.

**배열 필터를 이용한 갱신**

개별 배열 요소를 갱신하는 배열 필터인 arrayFilters가 있다.

특정 조건에 맞는 배열 요소를 갱신할 수 있다.

```jsx
db.blog.updateOne(
{"post" : post_id},
{$set: {"comments.$[elem].hidden" : true} },
{
  arrayFilters: [ {"elem.votes" : {$lte: -5}}]
}
)
```

- 반대포가 5표 이상인 댓글을 숨김처리 한다.

### 갱신 입력 (upsert)

갱신조건에 맞는 도큐먼트가 존재하지 않을때에는 새로운 도큐먼트를 생성한다.

upsert는 심지어 원자적이여서, 먼저 도착한 연산을 먼저 처리한다

```jsx
db.analytics.updateOne(
  {"url" : "/blog"}, 
  {"$inc" : {"pageviews" : 1}},
  {"upsert" : true}
)
```

### 다중 도큐먼트 갱신  - updateMany

updateMany는 스키마를 변경하거나 특정 사용자에 새로운 정보를 추가할 때 쓰기 좋다

```jsx

db.users.updateMany(
 {birthday: "10/13/1978"},
 {"$set" : {"gift" : "Happy birthday!"}} )
```

### 갱신한 도큐먼트 반환

findAndMofiy가 있지만 이것보다 쉬운 메서드를 도입했다.

findOneAndDelete, findOneAndReplace, findOneAndUpdate

이 메서드들은 원자적이다.

findOneAndUpdate는 도큐먼트 수정전, 수정 후 반환 하는것을 옵션으로 지원한다.

```jsx
db.processes.findOneAndUpdate({"status" : "READY"},
{"$set" : {"status" : "RUNNING"}},
{"sort" : {"priority" : -1}}
)
```

returnNewDocumentment 필드를 true 설정시 갱신된 도큐먼트 반환한다.

```jsx
db.processes.findOneAndUpdate({"status" : "READY"},
{"$set" : {"status" : "RUNNING"}},
{"sort" : {"priority" : -1}
"returnNewDocument" : true
},

)
```

findOneAndReplace는 동일한 매개변수를 사용하며 returnNewDocument 값에 따라 교체 전이나 후에 필터와 일치하는 도큐먼트를 반환한다.

findOneAndDelete도 유사하지만, 갱신 도큐먼트를 매개변수로 사용하지 않고, 삭제된 도큐먼트를 반환한다.

# 4 쿼리

- $ 조건절을 이용해 범위 쿼리, 셋의 포함 관계, 부등 관계 쿼리 등을 수행한다.
- 쿼리는 필요할 때마다 도큐먼트 배치를 반환하는 데이터베이스 커서를 반환한다.
- 커서를 이용해 결과를 몇 개 건너뛰거나, 반환하는 결과 수를 제한하거나, 결과를 정렬하는 등 다양한 메타 연산을 수행한다.

## find 소개

find함수는 쿼리에 사용한다. 

쿼리는 컬렉션에서 도큐먼트의 서브셋(빈 컬렉션부터 컬렉션 전체까지)을 반환한다.

find의 첫 매개변수에 따라 어떤 도큐먼트를 가져올지 결정된다 .

쿼리 도큐먼트에 여러 키/값 쌍을 추가해 검색을 제한할 수 있다.

예를들어 age가 27인 모든 도큐먼트는

```jsx
db.users.find({"age" : 27})
```

여러 키 값 쌍이면 조건1 AND 조건 2 가 된다

```jsx
db.users.find({"username" : "joe", "age" : 27})
```

### 반환받을 키 지정

때때로 반환받은 도큐먼트 내 키/값 정보가 모두 필요하지 않다면 find 또는 findOne의 두번째 매개변수에 원하는 키를 지정하면 된다. 

- 네트워크 데이터 전송량과 도큐먼트 디코딩에 드는 시간과 메모리를 줄여준다

예를들어 username과 email만 받고싶다면

```jsx
db.users.find({}, {"username" : 1, "email" : 1}) 
```

_id값은 지정하지 않아도 항상 반환된다.

반환을 제외시키려면?

```jsx
db.users.find({}, {"username" : 0}) 
```

### 제약 사항

쿼리 도큐먼트 값은 반드시 상수여야 한다. 

```jsx
db.stock.find({"in_stock" : "this.num_sold"}) /// 작동하지 않음 
```

## 4.2쿼리 조건

OR절, 부정 조건 등 다양하게 검색가능하다

### 쿼리 조건절

<, ≤, >, ≥ 에 해당하는 연산자는 각각 $lt, $lte, $gt, $gte다. 

예를들어 18세 이상 30세 이하라면

```jsx
db.users.find({"age" : {"$gte" : 18, "$lte" : 30}})
```

특정 값이 맞지 않는 도큐먼트를 찾을라면?

```jsx
db.users.find({"username" : {"$ne" : "joe"}})
```

### OR 쿼리

or쿼리는 2가지 방법이 있다.

$in과 $or이다.

$in에 조건 배열을 사용할 수 있다.

```jsx
db.raffle.find({"ticket_no" : {"$in" : [725, 542, 390]}})
```

서로 다른 데이터형도 쓸 수 있다.

$nin은 $in과 반대로 일치하지 않는 도큐먼트를 반환한다.

```jsx
db.raffle.find({"ticket_no" : {"$nin" : [725, 542, 390]}})
```

ticket_no가 725이거나 winner가 true인 도큐먼트를 찾고싶다면?

$or 연산자를 사용한다

```jsx
db.raffle.find({"$or" : [{"ticket_no" : 725}, {"winner" : true}]})
```

### $not - 메타조건절

$not은 어떤 조건에도 적용할 수 있다.

$mod는 키의 값을 첫번째 값으로 나눈 후 그 나머지 값의 조건을 두번째 값으로 기술하는 연산자다

```jsx
db.users.find({"id_num" : {"$mod" : [5, 1]}})
```

- id_num값이 1, 6, 11, 16 등 사용자 반환

만약 이것의 반대로 2, 3, 4, 5, 7 8 등을 받고싶다면?

```jsx
db.users.find({"id_num" : {"$not" :  {"$mod" : [5, 1]}}})
```

## 형 특정 쿼리

다양한 데이터 형에 특정한 쿼리를 작동하게 할 수 있다. 

### null

null은 스스로와 일치하는것을 찾는다.

y가 null인 도큐먼트 쿼리

```jsx
db.c.find({"y" : null})
```

그러나 null은 존재하지 않음과도 일치한다. 즉 키가 null인 값을 쿼리하면 해당 키를 갖지 않는 도큐먼트도 반환한다. 

만약 값이 null인 키만 찾고 싶다면 키가 null인 값을 쿼리하고 $exists 조건절을 사용해 null 존재 여부를 확인하면 된다

```jsx
db.c.find({"z" : {"$eq" : null, "$exists" : true}})
```

### 정규표현식 - $regex

```jsx
db.users.find({"name" : {"$regex" : /joe/i}})
```

- Like abc% 처럼 접두사 정규 표현식에는 인덱스를 사용할 수 있다.

### 배열에 쿼리하기

배열 요소 쿼리는 스칼라 쿼리가 같은 방식으로 동작하도록 설계됐다.

```jsx
db.food.find({"fruit" : "banana"})
```

**$all 연산자**

2개이상의 배열 요소가 일치하는 배열을 찾으려면 $all을 사용한다. 이는 배열 내 여러 요소와 일치하는지 확인해준다.

```jsx
db.food.find({fruit : {$all : ["apple", "banana"]}})
```

**$size 연산자**

$size는 특정 크기의 배열을 쿼리하는 유용한 조건절이다. 

```jsx
db.food.find({"fruit" : {"$size" : 3}})
```

**일치하는 배열 요소의 반환**

블로그에서 Bob이 쓴 댓글을 얻으려면?

```jsx
db.blog.posts.find({"comments.name" : "bob"}, {"comments.$" : 1})
```

만약 배열 요소의 범위를 쿼리하고 싶다면 $eleMatch 연산자를 사용한다.

- $elemMatch 연산자는 비배열 요소는 일치시키지 않는다

```jsx
db.test.find({"x" : {"$eleMatch" : {"$gt" : 10, "$lt" : 20}}})
```

일반적으로, 배열을 포함하는 도큐먼트에 범위 쿼리를 할 때 min 함수와 max 함수를 사용하면 좋다. 배열에 대한 "$gt"/"Lt" 쿼리의 인덱스 한계mdes bound는 비효율적이다. 어떤 값이든 허 용하므로 범위 내 값뿐 아니라 모든 인덱스 항목index cniy을 검색한다.

### 내장 도큐먼트 (서브 도큐먼트) 쿼리하기

내장 도큐먼트 쿼리는 이름이 정확히 일치해야 한다.

가능하다면 특정 키로 쿼리하는 방법이 좋다.

```jsx
db.people.find({"name.first" : "joe", "name.last" : "Schmoe"})
```

만약 5점 이상을 받은 joe의 댓글을 찾는다고 가정하면?

구조

```jsx
{
    "content": "...",
    "comments": [
        {
            "author": "joe",
            "score": 3,
            "comment": "nice post"
        },
        {
            "author": "mary",
            "score": 6,
            "comment": "terrible post"
        }
    ]
}
db.blog.find({"comments" : {"author" : "joe", "score" : {"$gte : 5"}}})

db.blog.find({"comments.author", : "joe", "comments.score" : {"$gte" : 5}})
// 사용 불가
```

- 이 쿼리는 `comments` 배열 전체가 `{ "author": "joe", "score": { "$gte": 5 } }`와 일치하는지 확인합니다. 하지만 `comments`는 배열이므로, 배열의 한 요소만이 이 조건을 만족하는 것이 아니라 배열 전체가 이 조건과 일치해야 합니다. 이는 거의 불가능한 조건입니다.
- 이 쿼리는 `comments.author`가 `"joe"`이고 `comments.score`가 `5` 이상의 값을 가진 문서를 찾으려고 합니다. 이 경우, MongoDB는 `comments` 배열의 서로 다른 요소에서 `author`와 `score` 조건을 각각 찾을 수 있습니다. 즉, 하나의 객체에서 `author`가 `"joe"`이고 다른 객체에서 `score`가 `5` 이상일 수 있으므로, 이 쿼리는 의도한 대로 작동하지 않을 수 있습니다.

위 문제를 해결하기 위해서는 `$elemMatch` 연산자를 사용하여 배열의 동일한 객체 내에서 여러 조건을 결합할 수 있습니다.

다음과 같은 쿼리를 사용하십시오:

```jsx
javascript코드 복사
db.blog.find(
  {"comments": {$elemMatch: {"author": "joe", "score": {$gte: 5}}}
}
)

```

이 쿼리는 `comments` 배열 내의 객체 중 `author`가 `"joe"`이고 `score`가 `5` 이상인 객체가 있는 문서를 찾습니다. `$elemMatch`를 사용하면 배열 내의 단일 객체에서 조건을 결합하여 검사할 수 있습니다.

## $where 쿼리

$where은 임의의 자바스크립트를 쿼리 일부분으로 실행해가지고 보안상으로 사용을 제한해야 한다.

주로 도큐먼트 내 두 키의 값을 비교하는 쿼리에 가장 자주 쓰인다.

또한 일반 쿼리보다 느리니 피요한 경우가 아니면 쓰지 말자. 

두 필드의 값이 동일한 도큐먼트를 where로 쿼리

```jsx
db.foo.find({
    "$where": function() {
        for (var current in this) {
            for (var other in this) {
                if (current != other && this[current] == this[other]) {
                    return true;
                }
            }
        }
        return false;
    }
})
```

## 커서.

커서를 이용해 결과 개수를 제한하거나 결과를 ㄷ몇개 건너뛰거나 등 할 수 있다. 

cursor.hasNext()를 호출해야 비로소 그때 쿼리가 서버로 전송된다.

한번ㅂ에 보통 100개 또는 4메가바이트 크기의 결과 둘 중 작은것을 가져온다.

### limit 건너뛰기, 정렬(sort)

```jsx
db.c.find().limit(3)
```

skip은 처음 3개를 건너뛴 나머지를 반환한다

```jsx
db.c.find().skip(3)
```

sort는 매개변수로 받는다 

1은 오름차순, -1은 내림차순이다. 

ex) username은 오름차순, age는 내림차순

```jsx
db.c.find().sort({username : 1, age : -1})
```

세 메서드를 조합해 페이지네이션 할 수 있다.

ex) 가격을 내림차순으로 페이지당 50개 결과

```jsx
db.stock.find({"desc" : "mp3"}).limit(50).sort({"price" : -1})
```

갯수를 건너뛰고 다음페이지로 가고싶다면?

```jsx
db.stock.find({"desc" : "mp3"}).limit(50).skip(50).sort({"price" : -1})
```

- 큰 수를 건너뛰면 비효율적이다.  rdb처럼 이미 읽고 건너뛴 데이터를 폐기하기 때문이다.

### skip 하지 않고 페이지네이션 페이지 나누기 - Cursor-based Pagination

쿼리에 따라 skip을 사용하지 않을 수 있다.

예를들어 “date”를 내림차순으로 정렬

```jsx
var page1 = db.foo.find().sort({"date" : -1}).imit(100)
```

마지막 도큐먼트의 date값을 사용해 다음 페이지를 가져온다

```jsx
var latest = null;

while (page1.hasNext()) {
  latest = page1.next();
  
}

var page2 = db.foo.find("date" : {"$lt" : latest.date}});
page2.sort({"date" : -1}).limit(100)
```

커서 기반으로 쓰는것이다 

MongoDB에서 페이지네이션을 구현하는 여러 가지 방법이 있습니다. 커서 기반 페이지네이션 외에도 몇 가지 방법을 더 살펴보겠습니다:

### 1. **Range-based Pagination (범위 기반 페이지네이션)**

범위 기반 페이지네이션은 특정 필드 값을 기준으로 범위를 설정하여 페이지네이션을 수행합니다. 가장 흔하게 사용하는 필드는 `_id`입니다. `_id`는 기본적으로 ObjectId이며, 이는 생성된 시간에 따라 정렬될 수 있습니다.

### 예시

첫 페이지를 가져오는 쿼리:

```jsx
var page1 = db.collection.find().sort({_id: 1}).limit(10);

```

두 번째 페이지를 가져오는 쿼리 (이전 페이지의 마지막 `_id`를 이용):

```jsx
var last_id = page1[page1.length - 1]._id;
var page2 = db.collection.find({_id: {$gt: last_id}}).sort({_id: 1}).limit(10);

```

### 2. **Time-based Pagination (시간 기반 페이지네이션)**

특정 날짜 필드를 기준으로 페이지네이션을 수행하는 방법입니다. 예를 들어, `createdAt` 필드를 기준으로 하는 경우입니다.

### 예시

첫 페이지를 가져오는 쿼리:

```jsx
var page1 = db.collection.find().sort({createdAt: -1}).limit(10);

```

두 번째 페이지를 가져오는 쿼리 (이전 페이지의 마지막 `createdAt`을 이용):

```jsx
var last_date = page1[page1.length - 1].createdAt;
var page2 = db.collection.find({createdAt: {$lt: last_date}}).sort({createdAt: -1}).limit(10);

```

### 3. **Bookmark-based Pagination (북마크 기반 페이지네이션)**

사용자가 특정 문서로부터 페이지네이션을 시작할 수 있도록 하는 방식입니다. 이 방법은 범위 기반 페이지네이션과 유사하지만, 사용자가 직접 북마크를 설정합니다.

### 예시

첫 페이지를 가져오는 쿼리:

```jsx
var page1 = db.collection.find().sort({_id: 1}).limit(10);

```

북마크로부터 페이지를 가져오는 쿼리:

```jsx
var bookmark_id = /* 북마크로 저장한 _id 값 */;
var pageFromBookmark = db.collection.find({_id: {$gt: bookmark_id}}).sort({_id: 1}).limit(10);

```

### 4. **Aggregation Framework 기반 페이지네이션**

MongoDB의 Aggregation Framework를 사용하여 복잡한 페이지네이션을 구현할 수 있습니다. 이는 특히 여러 단계의 필터링과 정렬이 필요한 경우 유용합니다.

### 예시

첫 페이지를 가져오는 쿼리:

```jsx
var page1 = db.collection.aggregate([
    { $sort: { createdAt: -1 } },
    { $limit: 10 }
]);

```

두 번째 페이지를 가져오는 쿼리:

```jsx
var last_date = page1[page1.length - 1].createdAt;
var page2 = db.collection.aggregate([
    { $match: { createdAt: { $lt: last_date } } },
    { $sort: { createdAt: -1 } },
    { $limit: 10 }
]);

```

### 5. **Keyset Pagination**

Keyset Pagination은 범위 기반 페이지네이션의 변형으로, 두 개 이상의 필드를 조합하여 고유한 키를 생성하는 방식입니다. 이 방법은 복합 인덱스를 사용하여 성능을 최적화할 수 있습니다.

### 예시

첫 페이지를 가져오는 쿼리:

```jsx
var page1 = db.collection.find().sort({field1: 1, field2: 1}).limit(10);

```

두 번째 페이지를 가져오는 쿼리 (이전 페이지의 마지막 필드 값을 이용):

```jsx
var last_field1 = page1[page1.length - 1].field1;
var last_field2 = page1[page1.length - 1].field2;
var page2 = db.collection.find({
    $or: [
        { field1: { $gt: last_field1 } },
        { field1: last_field1, field2: { $gt: last_field2 } }
    ]
}).sort({field1: 1, field2: 1}).limit(10);

```

각 방법은 데이터의 특성과 사용 사례에 따라 적절히 선택되어야 합니다. 커서 기반 페이지네이션이 가장 일반적으로 사용되지만, 다른 방법들도 적절한 상황에서 매우 유용할 수 있습니다.

### 랜덤으로 도큐먼트 찾기

랜덤으로 스킵하고 가져오지 말고, Math.random()함수를 이용해서 가져오자

```jsx
db.people.findOne({"random" : {"$gt" : random}})
```

# 5장 인덱싱

인덱스를 사용하지 않는 쿼리를 컬렉션 스캔이라하며, 테이블 풀 스캔과 비슷하다.

explain 함수를 이용해 crud 작업의 실행정보를 볼 수 있다.

```jsx
db.users.find({"username" : "user101"}).explain("executionStats")
```

### 인덱스 생성

ex) username 필드에 인덱스 생성

```jsx
db.users.createIndex({"username" : 1})
```

인덱싱된 필드를 변경하는 삽입, 삭제, 갱신 작업은 오래걸린다. 

- 전형적인 관계형 데이터베이스 인덱스와 거의 동일하게 작동한다.

인덱스를 생성한 필드를 선택하려면, 자주 쓰는 쿼리와 빨리 수행해야 하는 쿼리를 조사해 공통 키 셋을 찾아본다.

### 복합 인덱스

인덱스는 모든 값을 정렬된 순서로 보관하므로 도큐먼트를 정렬하는 작업이 훨씬 빨라진다.

그러나 sort()에서 선두컬럼에 존재하지 않으면 정렬에 도움되지 않는다.

복합 인덱스 선언방법

```jsx
db.users.createIndex({"age" : 1, "username" : 1})
```

복합 인덱스를 구성할 때는 정렬 키를 첫 번 째에 놓으면 좋다. 이 방법은 동등 쿼리, 다중값 쿼리, 정렬을 고려해 복합 인덱스를 구성할 때 의 모범 사례다.

### 몽고 db가 인덱스를 선택하는 방법

5개의 인덱스가 있다. 쿼리가 들어오면 몽고는 쿼리 모양을 확인한다.

모양은 검색할 필드, 정렬 여부 등 추가 정보와 관련 있다.

이후 인덱스 5개중 3개가 쿼리 후보로 식별됐다고 가정해보자 

후보에 하나씩 3개 쿼리플랜을 만들고, 각 다른 인덱스를 사용하는 3개의 병렬 스레드에서 쿼리를 실행한다. 이후 가장 빠르게 결과를 반환하는 쿼리 플랜이 승자가 된다.

<img src="./images/Untitled 2.png"/>

서버는 쿼리 플랜의 캐시를 유지하는데, 승리한 플랜은 차후 모양이 같은 쿼리에 사용하기 위해 캐시에 저장된다. 시간이 지나 컬렉션과 인덱스가 변경되면 플랜이 캐시에서 제거되고, 다시 플랜을 찾는다.

인덱스를 추가하거나, 삭제해도 플랜이 캐시에서 제거된다.

- 명시적으로도 지울수있다

### 복합 인덱스 사용

인덱스의 선택성을 고려해야 한다.

..
쿼리 플랜에 "SORT" 단계가 표시된다면, 이는 몽고DB가 데이터베이스에서 결과 셋을 정렬할 때 인덱스를 사용할 수 없었으며 대신 인메모리 정렬을 했다는 의미다.

```jsx
"rejectedPlans": [
{
"stage": "SORT",
"sortPattern": {
  "student _id": 1
},

```

몽고디비는 특정 인덱스를 사용하도록 강제하는 두가지 방법을 제공한다.

커서 hint 메서드를 사용하면 모양이나 이름을 지정함으로써 사용할 인덱스를 지정할 수 있다.

planCacheSetFilter 함수를 인덱스 필터와 함께 사용하면, 쿼리 옵티마이저가 인덱스 필터에 지정된 인덱스만 고려하도록 제한할 수 있다. 

```jsx
db.student.find({student_id: {$gt: 500000}, class_id:54})
.sort({student_id: 1})
.hint({class_id:1})
.explain("executionStats")
```

일반적으로 동등 (=) 필터를 사용할 필드가 다중값 필터( 범위, range)를 사용할 필드보다 앞에 두도록 복합 인덱스를 설계하는것이 좋다. 스캔할 범위가 너무 많아지기 때문이다. 

또한 선정된 쿼리 플랜에 SORT 단계까 포함되면 인메모리 정렬하므로 일반적으로 느리다. 

피해야 한다. 

복합인덱스 설계시 트레이드 오프가 있다. 

인메모리 정렬을 피하려면, 반환하는 도큐먼트 개수보다 더 많은 키를 검사해야 한다.

인덱스를 사용해 정렬하려면 몽고 디비가 인덱스 키를 순서대로 살펴볼 수 있어야 한다.

- 즉 복합 인덱스 사이에 정렬 필드를 포함해야 한다.

결론적으로 복합인덱스를 설계할 떄

- 동등 필터에 대한 키를 맨 앞에 표시해야 한다.
- 정렬에 사용되는 키는 다중값 필드 앞에 표시해야 한다.
- 다중값 필터에 대한 키는 마지막에 표시해야 한다.

### 커버드 쿼리 사용하기

인덱스가 쿼리가 요구하는 값을 모두 포함하면, 쿼리가 커버드 된다고 한다

(커버링과 비슷한듯)

쿼리가 확실히 인덱스만 사용하게 하려면 _id필드를 반환받지 않도록 반환받을 키를 지정해야 한다.

커버드 쿼리에 explain시, 결과에 FETCH단계의 하위 단계까 아닌 IXSCAN단계까 있으며, executionStatus는 totalDocsExamined의 값이 0이 된다.

## 5.1.5 $연산자의 인덱스 사용법

부정조건은 비효율적이다. $ne 쿼리는 인덱스를 사용하긴 하지만 잘 활용하진 못한다.

$not을 사용하는 쿼리 대부분은 테이블 스캔을 수행한다. $nin은 항상 테이블 스캔을 수행한다

OR 쿼리

현재 몽고디비는 쿼리당 하나의 인덱스만 사용할 수 있다.

그러나 $or는 다르다. 두 개의 쿼리를 수행하고 결과를 합치므로 $or절마다 인덱스를 사용할 수 있다.

```jsx
db.foo.find({"$or" : [{"x" : 123}, {"y" : 456}]}).explain()
```

### 객체 및 배열 인덱싱

내장 도큐먼트(서브 도큐먼트)에도 인덱스가 생성될 수 있다.

```jsx
{
  "username": "sid",
  "loc": {
    "ip": "1.2.3.4",
    "city": "Springfield",
    "state": "NY"
  }
}

db.users.createIndex({"loc.city" : 1})
```

만약 서브 도큐먼트 전체를 인덱싱하면, 서브 도큐먼트 전체에 쿼리할때만 도움이 된다.

위 예제에서 ip나 city 에는 쿼리에 인덱스 사용할 수 없다

배열 인덱싱

각 도큐먼트가 하나의 게시물인 블로그 컬렉션이 있다고 가정하자.

각 게시물은 comments 필드를 갖는다.

가장 최근에 댓글이 달린 블로그 게시물을 찾으려면

```jsx
db.blog.createIndex({"comments.date" : 1})
```

배열을 인덱싱하면, 배열의 각 요소에 인덱스 항목을 생성하므로 한 게시물에 20개의 댓글이 달렸다면, 도큐먼트는 20개의 인덱스 항목을 가진다. 때문에 인덱스가 매우 부담스러워진다.

### 인덱스 카디널리티

카디널리티가 높을수록 인덱싱이 도움이 된다. 

복합인덱스에서는 높은 카디널리티 키를 선두에 두자.

## explain 출력

쿼리에 대한 정보를 제공하며, 느린 쿼리를 잡을 수 있는 진단 도구다.

다음은 예시 쿼리와 `explain` 명령어의 결과를 분석하는 방법입니다:

```jsx
db.users.find({ age: { $gt: 25 } }).explain("executionStats")
```

이 쿼리의 실행 계획 결과는 다음과 같이 나올 수 있습니다:

```json
{
  "queryPlanner": {
    "plannerVersion": 1,
    "namespace": "test.users",
    "indexFilterSet": false,
    "parsedQuery": {
      "age": {
        "$gt": 25
      }
    },
    "winningPlan": {
      "stage": "FETCH",
      "inputStage": {
        "stage": "IXSCAN",
        "keyPattern": {
          "age": 1
        },
        "indexName": "age_1",
        "isMultiKey": false,
        "multiKeyPaths": {
          "age": []
        },
        "isUnique": false,
        "isSparse": false,
        "isPartial": false,
        "indexVersion": 2,
        "direction": "forward",
        "indexBounds": {
          "age": [
            "(25, inf.0]"
          ]
        }
      }
    },
    "rejectedPlans": []
  },
  "executionStats": {
    "executionSuccess": true,
    "nReturned": 100,
    "executionTimeMillis": 1,
    "totalKeysExamined": 100,
    "totalDocsExamined": 100,
    "executionStages": {
      "stage": "FETCH",
      "nReturned": 100,
      "executionTimeMillisEstimate": 0,
      "works": 101,
      "advanced": 100,
      "needTime": 0,
      "needYield": 0,
      "saveState": 0,
      "restoreState": 0,
      "isEOF": 1,
      "docsExamined": 100,
      "alreadyHasObj": 0,
      "inputStage": {
        "stage": "IXSCAN",
        "nReturned": 100,
        "executionTimeMillisEstimate": 0,
        "works": 101,
        "advanced": 100,
        "needTime": 0,
        "needYield": 0,
        "saveState": 0,
        "restoreState": 0,
        "isEOF": 1,
        "keyPattern": {
          "age": 1
        },
        "indexName": "age_1",
        "isMultiKey": false,
        "multiKeyPaths": {
          "age": []
        },
        "isUnique": false,
        "isSparse": false,
        "isPartial": false,
        "indexVersion": 2,
        "direction": "forward",
        "indexBounds": {
          "age": [
            "(25, inf.0]"
          ]
        },
        "keysExamined": 100,
        "seeks": 1,
        "dupsTested": 0,
        "dupsDropped": 0,
        "seenInvalidated": 0
      }
    }
  },
  "serverInfo": {
    "host": "localhost",
    "port": 27017,
    "version": "4.2.6",
    "gitVersion": "20364840b8f1af16917e4c23c1b5f5efd8b352f8"
  },
  "ok": 1
}

```

winningPlan의 inputStage.indexName은 사용한 인덱스명이다 

executionStats의 nReturned는 실제로 반환된 도큐먼트 수다.

totalDocsExamined는 검색한 도큐먼트 수이다.

nscannedObjects는 스캔한 도큐먼트 수다.

executionTimeMillis는 서버가 요청을 받고 응답을 보낸 시점까지 쿼리 실행됐는지다.

isMultiKey : 다중키 인덱스 사용 여부

stage: IXSCAN : 인덱스를 사용해 쿼리할 수 있었는지 여부. COLSCAN은 인덱스로 쿼리할 수 없어 컬렉션 스캔을 수행했음을 의미 

쿼리에 힌트를 줘서 실행시킬수도 있다.

```jsx
db.users.find({"age" : 14, "username" : /.*/})
.hint({"username" : 1, "age" : 1})
```

- _ 원하는 인덱스를 사용하려고 힌트를 사용하려면 힌트가 적용된 리가 배포되기 전에 explain을 실행해야 한다

## 인덱스를 생성하지 않는 경우

대체로 쿼리가 컬렉션의 30%이상을 반환하는 경우 인덱스는 종종 속도를 높이기도 한다

| 인덱스가 적합한 경우 | 컬렉션 스캔이 적합한 경우 |
| --- | --- |
| 큰 컬렉션 | 작은 컬렉션 |
| 큰 도큐먼트 | 작은 도큐먼트 |
| 선택적 쿼리 | 비선택적 쿼리 |

## 인덱스 종류

### 유니크 인덱스

만약 도큐먼트에 firstname 키에 동일한 값을 가질 수 없도록 하려면?

firstname 필드가 있는 도큐먼트에 대해서만 partialFilterExpression으로 생성

```jsx
db.users.createIndex({"firstname" : 1},
 {"unique" : true, "partialFilterExpression" : {
   "firstname" : {$exists : true}
 } 
}
)
```

만약 중복으로 넣으면 중복키 예외가 발생한다.

복합 고유 인덱스도 가능하다.

### 부분 인덱스 (부분 유니크 인덱스)

고유 인덱스는 null을 값으로 취급하므로, 키가 없는 도큐먼트가 여러개인 고유 인덱스를 만들 수 없다.

고유한 필드가 존재하거나 필드가 아에 존재하지 않으면 unique와 partial을 결합할 수 있다.

```jsx
db.users.ensureIndex(
 {"email" : 1}, {"unique" : true , partialFilterExpression : 
   {email : {$exists : true}}
 }
)
```

## 인덱스 관리

createIndex를 이용해 인덱스를 만든다.

인덱스는 컬렉션당 한번만 만들어야 한다. 동일한 인덱스를 다시 생성하면 아무일도 일어나지 않는다.

모든 인덱스 정보는 system.indexses 컬렉션에 저장된다.

인덱스 생성 시 System. indexes에서 인덱스의 메타 정보를 확인할 수 있다. 특정 컬렉션의 모 든 인덱스 정보를 확인하려면 db.컬렉션명.getIndexes()를 실행한다.

### 인덱스 변경

인덱스 삭제 : dropIndex

```jsx
db.people.dropIndex("x_1_y_1")
```

background 옵션을 사용하면 읽기와 쓰기에 영향을 미치지 않고 처리한다.

가능하다면 기존 도큐먼트에 인덱스를 생성하면 인덱스를 먼저 생성하고 도큐먼트를 넣을때보다 좀 빠르다.

# 특수 인덱스와 컬렉션 유형

- 큐 같은 데이터를 위한 제한 컬렉션
- 캐시를 위한 TTL 인덱스
- 단순 문자열 검색을 위한 전문 인덱스
- 2D 구현 및 구면 기하학sphenical geomety을 위한 공간 정보 인덱스
- 대용량 파일 저장을 위한 GridFS

## 공간 정보 인덱스

몽고는 2dsphere와 2d라는 공간 정보 인덱스를 가진다

- 2dsphere : WGS84 좌표계를 기반으로 지표면을 모델링
    - 2d인덱스 사용할때보다 더 정확한 거리 계산을 할 수 있다.

2차원 평면의 point에는 2d 인덱스를 사용하자.

2dsphere를 사용하면 GeoJson형식(http://geojson.org)으로 점, 선, 다각형의 기하 구조를 표현할 수 있다. 

point는 longitude(경도)와 위도(latitude) 좌표를 요소로 갖는 배열 [경도, 위도] 로 표현된다

```jsx
{
  "name" : "newyork",
  "loc" : {
    "type" : "Point",
    "coordinates" : [50, 2]
  }

}
```

선은 점의 배열로 표현된다 

```jsx
{
  "name" : "newyork",
  "loc" : {
    "type" : "LineString",
    "coordinates" : [[0, 1], [0, 2], [1, 2]]
  }

}
```

다각형은 Polygon으로 표현되는 type이 다르다

createIndex와 함께 "2dsphere"를 사용해 공간 정보 인덱스를 만들 수 있다.

```jsx
db.openStreetMap.createIndex({"loc" : "2dsphere"})
```

### 공간 정보 쿼리 유형

교차(intersection), 포함 (within), 근접 (nearness)라는 세가지 유형이 있다.

찾을 항목을 {"$geometry" : geoJsonDesc}와 같은 GeojSON 객체로 지정한다.
예를 들어 "$geoIntersects" 연산자를 사용해 쿼리 위치와 교차하는 도큐먼트를 찾을 수 있다.

```jsx
db.openStreetMap.find(
{"loc" : {"$geoIntersects" : {"$geometry" : eastVillage}}}
)
```

뉴욕New Yortk Civ의 이스트빌리지 ilige 내에 한 점을 갖는 점, 선, 다각형이 포함된 도큐먼트를 모두 찾는다.

"$geoWithin"을 사용해 특정 지역에 완전히 포함된 항목을 쿼리할 수 있다 (예를 들면 이스트 빌리지에 있는 식당).

```jsx
db.openStreetMap.find(
{"loc" : {"$geoWithin" : {"$geometry" : eastVillage}}})
```

$near를 사용해 주변 위치에 쿼리할 수 있따.

```jsx
db.openStreetMap.find({"loc" : {"$near" : {"$geometry" : eastVillage}}})
```

### 공간 정보 인덱스 사용

사용자가 뉴욕에서 레스토랑을 찾도록 돕는 모바일 애플리케이션을 설계한다고 가정하자. 

- 사용자가 현재 위치한 지역을 찾는다.
- 해당 지역 내 레스토랑 수를 보여준다.
- 지정된 거리 내에 있는 레스토랑을 찾는다

| 쿼리 유형 | 기하 구조 유형 |
| --- | --- |
| $near (GeoJSON point, 2dsphere 인덱스) | 구면 |
| $near (레거시 좌표, 2d 인덱스) | 평면 |
| $geoNear (GeoJSON point, 2dsphere 인덱스) | 구면 |
| $geoNear (레거시 좌표, 2d 인덱스) | 평면 |
| $nearSphere (GeoJSON point, 2dsphere 인덱스) | 구면 |
| $nearSphere (레거시 좌표, 2d 인덱스) | 구면 |
| $geoWithin : { $geometry: ... } | 구면 |
| $geoWithin : { $box: ... } | 구면 |
| $geoWithin : { $polygon: ... } | 평면 |
| $geoWithin : { $center: ... } | 평면 |
| $geoWithin : { $centerSphere: ... } | 구면 |
| $geoIntersects | 구면 |

2d 인덱스는 구(sphere)에서 평면 기하학과 거리 계산을 모두 지원한다.

$geoNear 연산자는 집계 연산자다.

$near 쿼리 연산자는, 샤딩을 이용해 배포된 컬렉션에서는 작동하지 않는다

geoNear 명령과 $geoNear 집계 연산을 사용하려면 컬렉션에 2dsphere 인덱스와 2d 인덱스가 1개만 있어야 한다.

- $near나 $geoWithin은 상관 없다.

레스토랑 검색 예시

데이터 다운로드 : 

예제에서는 뉴욕에 위치한 지역([https://oreil.Ly/rpGna](https://oreil.ly/rpGna))과 레스토랑([https://oreil](https://oreil/).ly/JXYd-) 데이터셋을 이용해 작업한다. 깃허브(it)에서 예제 데이터셋을 다운로드할 수
있다.

다음과 같이 mongoimport 도구를 사용해 데이터셋을 데이터베이스로 가져오자.

```jsx
$ mongoimport ‹path to neighborhoods.json> -c neighborhoods 
$ mongoimport ‹path to restaurants. json> -c restaurants
docker cp /Users/ysk/Downloads/neighborhoods.json mongodb:/tmp/neighborhoods.json
docker cp /Users/ysk/Downloads/restaurants.json mongodb:/tmp/restaurants.json

# Replace <username> and <password> with your MongoDB username and password
docker exec -it mongodb mongoimport /tmp/neighborhoods.json -c neighborhoods --username root --password root --authenticationDatabase admin
docker exec -it mongodb mongoimport /tmp/restaurants.json -c restaurants --username root --password root --authenticationDatabase admin
```

이후 인덱스 만들고 조회

```jsx
test> db.neighborhoods.createIndex({location: "2dsphere"})
location_2dsphere
test> db.restaurants.createIndex({location: "2dsphere"})
location_2dsphere
test> db.neighborhoods.find({name: "Clinton"})
```

현재 지역 찾기

```jsx
db.neighborhoods.findOne({geometry: {$geoIntersects: {$geometry: {type: "Point", 
coordinates:[-73.93414657, 40.82302903]
}}}})
```

지역 내 모든 레스토랑 찾기

```jsx
var neighborhood = db.neighborhoods.findOne(
{geometry: {$geoIntersects: {$geometry: {type: "Point", 
coordinates:[-73.93414657, 40.82302903]
}}}});

db.restaurants.findOne(
{location: {
  $geoWithin: {
    $geometry: neighborhood.geometry
  }
}
}, {name: 1, _id: 0}
)
```

범위 내에서 레스토랑 찾기

$centerSphere와 함께 $geoWithin을 사용하면 정렬되지 않은 순서로 결과를 반환하며

$maxDistance와 함께 $nearSphere를 사용하면 거리 순으로 정렬된 결과를 반환한다.

사용자로부터 5마일 이내에 있는 모든 레스토랑

```jsx
db.restaurants.findOne(
{
  location: {
    $geoWithin: {
      $centerSphere: [
      [-73.93414657, 40.82302903],
      5/3963.2
      ]
    }
  }
})
```

- 3963.2마일은 대략적인 적도 반경.

아래처럼도 사용 가능.

```jsx
var METERS_PER_MILE = 1609.34;

db.restaurants.find(
{
  location: { // 찾고자 하는 위치 필드입니다.
    $nearSphere: { // 위치 필드에 대해 구면좌표계 (지구상의 거리)를 기준으로 검색을 수행
      $geometry: { // 검색의 중심점이 될 지리적 좌표
        type: "Point",
        coordinates: [-73.93414657, 40.82302903]
      
      },
      $maxDistance: 5 * METERS_PER_MILE // 중심점에서 최대 거리 (반경)로 5마일을 설정
    
    }
  
  }
});
```

### 복합 정보 공간 인덱스

공간 정보 인덱스도 다른 인덱스처럼 묶을 수 있다.

공간정보 인덱스 + 다른 필드.

- ex) 공간정보 + 태그값

```jsx
db.openStreetMap.createIndex(
 {"tags" : 1, "location" : "2dsphere"}
)

db.openStreetMap.find(
  {
    "loc" : {"$geoWithin" : 
      {"$geometry" : hellsKitchen.geometry}},
      "tags" : "pizza"
  }
)
```

### 2d인덱스

구체가 아닌 지도 에는 2dsphere 대신 2d 인덱스를 사용한다

```jsx
db.hyrule.createIndex({"tile" : "2d"})
```

2d인덱스는 지형이 구체가 아니라 완전히 평평한 표면이라고 가정하므로 구체에 2d인덱스를 사용해서는 안된다.

2d인덱스 맥이는 필드는 요소가 2개인 배열을 사용한다.

```jsx
{
  "name" : "Example",
  "tile" : [32, 22]
}
```

GeoJson 데이터를 저장하려면 2d인덱스를 사용하지 말자. 2d인덱스는 점(point)만 인덱싱할 수 있다.

기본적으로 2d이넥스는 값이 -180 ~ 180 사이에 있다고 가정한다. 범위를 변경하고 싶다면 createIndex를 사용해 범위를 지정한다

```jsx
db.hyrule.createIndex({"light-years" : "2d"}, {"min" : -1000, "max" : 1000})
```

2d 인덱스는 $geoWithin, $nearSphere(), $near 를 지원한다

$geoWithin : 평평한 표면 영역 내 점 쿼리. 

왼쪽 하단 모서리[10, 10]과 오른쪽 상담 모서리[100, 100]으로 정의된 사각형 내 에 대한 도큐먼트 쿼리

```jsx
db.hyrule.find( {
  tile: {
    $geoWithin: {
      $box: [[10, 10], [100, 100]]
    }
  }
})
```

중심이 -17.20.5이고, 반지름이 25인 원 안에 쿼리

```jsx
db.hyrule.find( {
  tile: {
    $geoWithin: {
      $center: [[-17, 20.5], 25]
    }
  }
})
```

$near : 특정 지점으로부터 가장 가까운 좌표 쌍을 포함하는 도큐먼트 쿼리

결과를 거리순으로 정렬한다

```jsx
db.hyrule.find({"tile" : {"$near" : [20, 21]}})
```

기본적으로 100개로 제한되며, limit(10)으로 줄일수도 있다. 

$nearSphere(), $near

## 6.2 전문 검색을 위한 인덱스

Atlas Full Text Search Index와 혼동하지 말자. 루씬으로 구현한 기능이 아니다.

text인덱스는 텍스트를 빠르게 검색하는 기능을 제공하며, tokenization, stop-word, 형태소 분석 등 일반적인 검색 엔진 요구 사항을 지원한다.

text인덱스에서 필요한 키의 개수는 인덱싱되는 필드의 단어 개수에 비례한다.

- 문자열이 토큰화, 형태소화 되며 여러 위치에서 갱신됌
- 때문에 시스템 리소스가 많이 소비될 수 있다.

text로 인덱싱된 컬렉션에서는 쓰기 성능이 다른 컬렉션보다 좋지 않다.

샤딩을 하면 데이터 이동 속도가 느려지며, 모든 텍스트는 새 샤드로 마이그레이션 될 때 다시 인덱싱 되야 한다.

### 6.2.1 텍스트 인덱스 생성

```jsx
db.articles.createIndex({"title" : "text", "body" : "text"})
```

$**에 인덱스를 만들면 도큐먼트 내 모든 문자열 필드에 전문 인덱스를 생성한다.

```jsx
db.articles.createIndex({"$**" : "text"})
```

### 6.2.2 텍스트 검색

$text 쿼리 연산자를 사용해 텍스트 검색을 수행한다. $text는 공백과 구두점을 delmeter로 사용해 검색 문자열을 토큰화 하며, 모든 토큰의 논리적 OR을 수행한다.

```jsx
db.articles.find(
  {"$text" : {"$search" : "impact creater lunar"}},
  {title: 1}
).limit(10)
```

이렇게 하면 impact or crater or lunar 라는 용어가 포함된 모든 기사를 찾는다.

너무 광범위해서 효율적이지 못할뿐더라

텍스트 검색이 기본적으로 결과를 관련성에 따라 정렬하지 못한다.

다음처럼 구문을 사용해 문제를 해결할 수 있다. 텍스트를 큰따옴표로 묶여서 정확히 일치하는 구문을 검색할 수 있따.

```jsx
db.articles.find(
  {"$text" : {"$search" : "\"impact creater\" lunar"}},
  {title: 1}
).limit(10)
```

mongodb는 이렇게 쿼리하면 impact crater AND lunar로 처리한다. 

- 논리적 AND 라고 한다

그렇다면 결과과 관련성에 따라 어떻게 정렬할 수 있을까?

관련성 스코어는 textScore라는 메타 데이터 필드에 저장된다

```jsx
db.articles.find(
  {"$text" : {"$search" : "\"impact creater\" lunar"}},
  {title: 1, score: {$meta: "textScore"}}
).limit(10)
```

때문에 추가적으로 sort 함수를 호출해야 한다 

```jsx
db.articles.find(
  {"$text" : {"$search" : "\"impact creater\" lunar"}},
  {title: 1, score: {$meta: "textScore"}}
)
.sort({score: {$meta: "textScore"}})
.limit(10)
```

### 전문 검색 최적화

두 방법이 있다.

1. 다른 기준으로 검색 결과를 좁힐 수 있다면, 복합 인덱스 생성시 다른 기준을 첫번째로 두고 전문 필드를 두번쨰로 둔다

```jsx
db.blog.createIndex({"date" : 1, "post" : "text"})
```

전문 인덱스를 date에 따라 몇개의 tree로 쪼개어 파티셔닝 하여 쿼리를 빨리 할 수 있다.

또한 다른 기준을 뒤쪽에 둔다.

```jsx
db.blog.createIndex({"post" : "text", "author" : 1})
```

### 다른 언어로 검색하기

text 인덱스에는 default_language 옵션을 지정할 수 있고 기본값은 english다

최신 목록을 보려면 : [https://docs.mongodb.com/manual/reference/text-search-languages/#text-search-languages를](https://docs.mongodb.com/manual/reference/text-search-languages/#text-search-languages를) 참조한다.

ex 프랑스어 인덱스

```jsx
db.users.createIndex({"profil" : "text", 
"interets" : "text"},
 {"default_language" : "french"}
)
```

## 제한 컬렉션

일반적인 컬렉션은 동적으로 생성되고 추가적인 데이터에 크기에 맞춰 늘어난다.

제한 컬렉션은 미리 생성돼 크기가 고정되어 원형 큐처럼 동작한다.

빈 공간이 없으면 가장 오래된 도큐먼트가 지워지고, 새로운 도큐먼트가 그 자리를 차지한다.

도큐먼트는 삭제할 수 없고, 크기가 커지도록 갱신도 허용되지 않는다. 

또한 제한 컬렉션은 샤딩될 수 없다.

### 제한 컬렉션 생성

```jsx
db.createCollection(
"my_collection", {"capped" : true, "size" : 100000
})
```

10만바이트 고정 크기 

도큐먼트 수에 제한을 지정하려면?

```jsx
db.createCollection(
"my_collection", {"capped" : true, "size" : 100000, "max" : 100
})
```

제한 컬렉션은 생성되면 변경할 수 없다. 

- 컬렉션은 max보다 많은 도큐먼트를 가지거나, size 이상의 공간을 차지할 수 없다

일반 컬렉션도 제한 컬렉션으로 변경할 수 있다.

```jsx
db.runCommand({"convertToCapped" : "test", "size" : 10000});
```

### 꼬리를 무는 커서 -tailable cursor

결과를 모두 꺼낸 후에도 종료되지 않는 커서. tail -f 랑 비슷하다.

일반 컬렉션에서는 입력 순서가 추적되지 않기떄문에, 제한 컬렉션에서만 사용한다.

아무런 결과가 없다면 10분 후에 종료된다.

## TTL 인덱스

TTL을 이용해서 각 도큐먼트에 유효 시간을 설정할 수 있다.

createIndex의 두번째 인자에 expireAfterSeconds를 이용해 생성할 수 있다.

```jsx
db.sessions.createIndex({"lastUpdated" : 1},
 {"expireAfterSeconds" : 60 * 60 * 24})
```

- lastUpdated 필드가 존재하고 날짜형이라면 서버 시간이 도큐먼트의 expireAfterSeconds를 지나면 도큐먼트가 삭제된다.

몽고DB는 TTL 인덱스를 매분마다 청소하므로 초 단위로 신경 쓸 필요 없다.

collMod를 이용해 변경할 수 있다.

```jsx
db.runCommand({"collMod" : "someapp.cache",
  "index" : {"keyPattern" : {"lastUpdated" : 1}, 
  "expireAfterSeconds" : 3600}
})
```

하나의 컬렉션에 TTL 인덱스를 여러 개 가질 수 있다. 

복합 인덱스는 될 수 없지만 정렬 및 쿼리 최적화가 목적이라면 '일반' 인덱스처럼 사용될 수 있다.

## GridFs로 파일 저장하기

gridFs는 대용량 이진 파일을 저장하는 메커니즘이다.

- 파일 스토리지를 위한 도구 대신 gridfs사용 가능
- 복제나 샤딩을 사용할 수 있음.

단점

- 성능이 느리다. 파일 시스템 직접 접근하는것보단 느리다.
- 도큐먼트를 수정하려면, 도큐먼트 전체를 삭제하고 다시 저장하는 방법뿐이다.
    - 몽고 디비는 파일을 여러개의 도큐먼트로 저장하므로, 한 파일의 모든 청크에 락을 걸 수 없다.

### GridFs 시작하기 : mongofiles

mongofiles 유틸리티로 쉽게 설치하고 운영할 수 있다.

# 7 집계 프레임 워크

* Aggregation stage
* Aggregation expression
* Aggregation accumlator

## 파이프라인, 단계 및 조정 가능 항목

집계 프레임워크는 몽고 DB내 분석 도구 모음으로, 하나 이상의 컬렉션에 있는 도큐먼트에 대한 분석을 수행하게 해준다.

집계 파이프라인을 통해 몽고 DB 컬렉션에서 입력을 받고, 컬렉션에서 나온 도큐먼트를 하나 이상의 단계를 거쳐 전달하며, 단계마다 해당 입력에 다른 작업을 수행한다.

<img src="./images/Untitled 3.png"/>

집계 파이프라인의 개별 단계는 한번에 입력 도큐먼트 스트림을 하나씩 가져와서 각 도큐먼트를 하나씩 처리하고 출력 도큐먼트 스트림을 하나씩 생성한다.

요약하면 파이프라인은 몽고DB 컬렉션과 함께 작동한다. 파이프라인은 단계로 구성되며 각 단계는 입력에 서로 다른 데이터 처리 작업을 수행하고, 출력으로 도큐먼트를 생성해 다음 단 계로 전달한다. 

처리 작업 끝에서 생성한 출력은 애플리케이션에서 작업하는 데 사용하거나, 컬렉션에 보내 나중에 사용한다. 많은 경우에 필요한 분석을 수행하기 위해 단일 파이프라인에 동일한 유형의 단계를 여러 번 포함한다.

## 단계 시작하기

match ,projection, sort, skip, limit 단계를 보자

예제 데이터 : 회사 데이터 컬렉션

- 회사 이름, 회사 설명, 회사 설립 시기, 펀딩 라운드 등

2004년에 설립된 회사 찾기

```jsx
test> db.companies.aggregate([{$match: {founded_year: 2004}}])
```

도큐먼트당 몇개의 필드만을 나타내도록 출력 수정

```jsx
db.companies.aggregate([{$match: {founded_year: 2004}},
{
 $project: {
   _id:0,
   name: 1,
   founded_year: 1
  }
}
])
```

aggregate 파이프라인은 도큐먼트를 요소로 포함하는 배열이다.

일치단계 - 선출단계가 있다.

일치단계는 컬렉션에 대해 필터링하고, 결과 도큐먼트를 한번에 하나씩 선출단계로 전달한다.

파이프라인을 구축할때는 다음 단계로 넘어갈때 도큐먼트 수를 반드시 줄여 넘기는것이 좋다. 성능에 영향을 끼친다.

* 즉 먼저 limit을 하는것이 나중에 limit 하는것보다 좋다

순서가 중요하다면, limit 단계 전 정렬을 수행해야 한다

```json
db.companies.aggregate([
  {$match : {founded_year: 2004}},
  {$sort: {name : 1}},
  {$limit: 5},
  {$project: {
    _id: 0,
    name: 1
  }}
]
)
```

## 7.4 $project - 선출

중첩된 필드를 승격해서 같은 레벨로 올릴 수 있다.

$ 문자는 선출단계에서 값을 지정하고 필드에서 선출한다.

```
db.companies.aggregate([
...
{$project: {
	_id: 0,
	name: 1,
	ipo: "$ipo.pub_year",
	valuation: "${ipo.valuation_amount}"
}

}
])
```

## 7.5 $unwind - 전개

unwind는 입력 도큐먼트에서 배열을 가져오고 해당 배열의 각 요소에 대한 출력 도큐먼트를 생성한다.

즉 배열에 10개 요소가 있다면 전개 단계에서 10개의 출력 도큐먼트가 생성된다.  

## 7.7 누산기

누산기를 사용하면 특정 필드의 모든 값 `$sum`,  평균 `$avg` `$first`,  `$last`, `$max` , `$min` 등이 가능하다.

https://www.mongodb.com/ko-kr/docs/manual/reference/aggregation/ 를 보면 사용 가능한 누산 표현식을 볼 수 있다.

## 7.8 그룹화

그룹 단계는 SQL GROUP BY와 유사한 기능을 수행한다

```
db.compoanies.aggregate([
{$group: {
  _id: {founded_year : "$founded_year"},
  average_numbe_of_employees: {$avg : $number_of_employees}
}},
{$sort : {average_number_of_employees: -1}}
])
```

### 7.8.1 그룹 단계의 _id 필드

그룹화 할 값에 

**왜 레이블을 지정해야 할까요?**

1. **명확성 향상:**
   - **레이블 미지정:** 만약 `_id: "$founded_year"`로 설정하면, 결과 문서의 `_id` 필드에는 단순히 숫자(예: `1990`)만 들어가게 됩니다. 이 경우, 이 숫자가 무엇을 의미하는지(예: 설립 연도) 직관적으로 알기 어렵습니다.
   - **레이블 지정:** 반면, `_id: { founded_year: "$founded_year" }`로 설정하면, 결과 문서의 `_id` 필드에 `{ founded_year: 1990 }`와 같이 명확한 키-값 쌍이 들어가게 되어, 이 숫자가 설립 연도임을 즉시 이해할 수 있습니다.
2. **추가적인 그룹화 기준 가능:**
   - 레이블을 사용하면, 나중에 그룹화 기준을 확장하거나 변경할 때 유연하게 대응할 수 있습니다. 예를 들어, 설립 연도뿐만 아니라 다른 기준(예: 산업군 등)을 추가로 그룹화하려는 경우에도 구조화된 형태로 관리하기 용이합니다.

예시.

```
// 레이블 미지정
{
  "_id": 1990,
  "companies": ["Company A", "Company B"]
}

// 레이블 지정. 키가 founded_year라고 명확하게 알 수 있다. 
{
  "_id": { "founded_year": 1990 },
  "companies": ["Company A", "Company B"]
}

```

## 7.9 집계 파이프라인 결과를 컬렉션에 쓰기

생성된 도큐먼트를 껄렉션에 쓸 수 있는 두가지 단계로 `$out`과 `$merge`가 있다.

out은 동일한 데이터베이스는 쓸 수있고, 샤딩된 컬렉션에는 쓸 수 없다.

merge는 샤딩 여부 관계 없이 모든 데이터 베이스와 컬렉션에 쓸 수 있다. merge는 기존 컬렉션으로 새 도큐ㅓㅁㄴ트 삽입, 병합, 작업 실패, 기존 유지, 사용자 정의 갱신 을 모두 사용 가능하다. 

merge의 진정한 장점은 출력컬렉션의 내용이 점진적으로 갱신되는 구체화된 뷰를 생성할 수 있다.



# 8. 트랜잭션

몽고디비는 여러 작업, 컬렉션, 데이터베이스, 도큐먼트 및 샤드에서 ACID 호환 트랜잭션을 지원한다.

### 8.1 ACID 정의

atomicity(원자성) : 트랜잭션 내 모든 작업이 적용되거나 아무 작업도 적용되지 않도록 함. 부분 적용은 없음 

consistency(일관성): 트랜잭션이 성공하면 다음 일관성 있는 상태로 이동하도록 함 

isonlation(고립성) : 동시에 실행되도록 허용하는 속성으로 트랜잭션이 다른 트랜잭션의 부분 결과를 보지 않도록 함 

 durability(영속성) : 커밋되면 시스템 오류가 발생하더라도 모든 데이터가 유지되도록 함 

몽고DB는 레플리카셋과 샤드전체에 ACID 호환 트랜잭션이 있는 분산 데이터베이스다

* 트랜잭션에 필요한 기능을 구현하는 방법 : https://www.mongodb.com/transactions

### 트랜잭션 사용법

두가지 api를 지원한다

코어 API라는 start_transaction, commit_transaction이라는 구문을 제공하며, 재시도 로직을 제공하지 않고 개발자가 로직, 커밋함수, 필요한 재시도 및 오류 로직을 모두 작성해야 한다

콜백API는 트랜잭션 시작, 콜백함수로 제공된 함수 실행, 트랜잭션 커밋을 포함해 많은 기능을 래핑하는 함수를 제공

물론입니다! 아래에 **표 8-1 코어 API와 콜백 API 비교**를 표 형식으로 정리해드리겠습니다.

------

**표 8-1 코어 API와 콜백 API 비교**

| 항목               | 코어 API                                                     | 콜백 API                                                     |
| ------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **트랜잭션 관리**  | 트랜잭션을 시작하고 커밋하려면 명시적인 호출이 필요          | 트랜잭션을 시작하고 지정된 작업을 실행한 후 커밋(또는 오류 시 중단) |
| **오류 처리**      | - Transient TransactionError 발생- UnknownTransactionCommitResult 오류 발생 | - Transient TransactionError 발생- UnknownTransactionCommitResult 오류 발생 |
| **처리 로직 통합** | 처리 로직을 통합하지 않고, 대신 사용자 지정 오류 처리를 사용 | 처리 로직을 자동으로 통합                                    |
| **유연성**         | 통합하는 유연성을 제공하지 않음                              | 통합하는 유연성을 제공                                       |
| **세션 관리**      | 특정 트랜잭션을 위해 API로 전달되는 명시적 논리 세션 필요    | 특정 트랜잭션을 위해 API로 전달되는 명시적 논리 세션이 필요함 |

------

### 각 항목에 대한 상세 설명

1. **트랜잭션 관리**
   - **코어 API:** 트랜잭션을 시작하고 커밋하기 위해 개발자가 명시적으로 호출해야 합니다.
   - **콜백 API:** 트랜잭션을 시작하고 지정된 작업을 수행한 후, 성공 시 커밋하거나 오류 발생 시 자동으로 중단합니다.
2. **오류 처리**
   - 두 API 모두 `Transient TransactionError`와 `UnknownTransactionCommitResult` 오류를 처리할 수 있습니다. 하지만 처리 방식에 차이가 있을 수 있습니다.
3. **처리 로직 통합**
   - **코어 API:** 처리 로직을 통합하지 않고, 별도로 사용자 지정 오류 처리를 구현해야 합니다.
   - **콜백 API:** 처리 로직을 자동으로 통합하여 오류 처리를 보다 간편하게 관리할 수 있습니다.
4. **유연성**
   - **코어 API:** 처리 로직의 통합에 대한 유연성을 제공하지 않습니다.
   - **콜백 API:** 처리 로직을 통합하는 데 있어서 더 높은 유연성을 제공합니다.
5. **세션 관리**
   - **코어 API:** 특정 트랜잭션을 위해 API로 명시적인 논리 세션을 전달해야 합니다.
   - **콜백 API:** 특정 트랜잭션을 위해 API로 전달되는 명시적 논리 세션이 필요합니다.

## 8.3 애플리케이션을 위한 트랜잭션 제한 조정

몇가지 매개변수로 트랜잭션 최적을 조정할 수 있다.

### 8.3.1 타이밍과 Oplog 크기 제한

첫번째는 트랜잭션의 시간 제한, 락을 획득하려고 대기하는 시간, 트랜잭션 최대 길이를 제한하고

두번째는 oplog 항목과 개별 항목에 대한 크기 제한과 관련있다.

* **oplog**는 MongoDB의 **복제(replication)** 메커니즘에서 핵심적인 역할을 하는 **작업 로그(Operation Log)**입니다. 주로 **복제 세트(replica set)** 환경에서 사용되며, 데이터베이스의 변경 사항을 기록하고 이를 다른 복제본(replica members)으로 전파하는 데 활용



최대 실행 시간은 기본적으로 1분 이하이며, mogod 인스턴스레벨에서 transactionLifttimeLimitSeconds에 의해 제어된다. 샤드 클러스터의 경우 모든 샤드 복제 셋 멤버에 매개변수를 설정해야 한다. 

트랜잭션에 시간 제한을 명시적으로 설정하려면 commitTransaction에 maxTimeMS를 지정하는것이 좋다. 그렇지 않으면 transactionLifttimeLimitSeconds가 사용된다.

락을 획득하기 위해 트랜잭션이 대기하는 최대 시간은 기본적으로 5ms 이며 

이 값은 maxTransactionLockRequestTimeoutMillis에 의해 제어되는 제한을 수정해 늘릴 수 있다. 



몽고디비는 트랜잭션의 쓰기 작업에 필요한 만큼 oplog 항목을 생성한다. 각 oplog 항목은 BSON 도큐먼트 크기 제한인 16MB 이하여야 한다. 

### oplog의 구조 및 저장 방식

- **컬렉션 형태:**
  - oplog는 **`local`** 데이터베이스 내의 **`oplog.rs`** 컬렉션에 저장됩니다.
  - **Replica Set** 환경에서만 사용되며, **싱글 인스턴스**에서는 존재하지 않습니다.
- **TTL(Time-To-Live):**
  - oplog는 순환 로그 구조로 되어 있어, 저장 용량이 한정되어 있습니다.
  - 오래된 로그는 자동으로 삭제되며, 이를 통해 디스크 공간을 관리합니다.
  - oplog의 크기는 복제 세트의 데이터 변경 빈도와 필요한 복구 기간에 따라 조정할 수 있습니다.
- **형식:**
  - 각 로그 항목은 BSON 형식으로 저장되며, 다음과 같은 필드를 포함합니다:
    - **`ts`**: 타임스탬프
    - **`h`**: 해시 값
    - **`v`**: oplog 버전
    - **`op`**: 작업 유형 (예: `i` for insert, `u` for update, `d` for delete)
    - **`ns`**: 네임스페이스(데이터베이스와 컬렉션 이름)
    - **`o`**: 작업의 세부 사항

### oplog의 동작 원리

1. **쓰기 작업 발생:**
   - 클라이언트가 프라이머리 노드에 쓰기 작업을 요청합니다.
2. **oplog에 기록:**
   - 프라이머리 노드는 해당 쓰기 작업을 수행한 후, 그 작업을 oplog.rs 컬렉션에 기록합니다.
3. **세컨더리 노드로 전파:**
   - 세컨더리 노드들은 프라이머리 노드의 oplog를 주기적으로 **폴링(polling)** 하거나 **스트리밍(streaming)** 방식으로 읽어옵니다.
   - 읽어온 oplog 항목을 자신의 데이터베이스에 동일하게 적용하여 데이터 동기화를 유지합니다.
4. **동기화 완료:**
   - 세컨더리 노드가 모든 oplog 항목을 적용하면, 프라이머리 노드와 데이터 일관성을 유지하게 됩니다.



# 9 애플리케이션 설계

- ﻿스키마 설계 고려 사항
- ﻿﻿데이터 내장embed 방식과 참조 방식 중 결정하기
- ﻿﻿최적화를 위한 팁
- ﻿﻿일관성 고려 사항
- ﻿스키마 마이그레이션 방법
- ﻿﻿스키마 관리 방법
- ﻿﻿몽고DB가 데이터 스토리지로 적합하지 않은 경우

## 9.1 스키마 설계 고려 사항

가장 좋은 설계 접근 방식은 애플리케이션에서 원하는 방식으로 데이터를 표현하는 방법이며, 관계형 데이터베이스와 달리 스키마를 모델링하기 전에 먼저 쿼리 및 데이터 접근 패턴을 이해해야 한다.

스키마를 설계할 때 고려해야 하는 주요 오소들이다

* 제약 사항 : 도큐먼트 최대 크기는 16MB이며, 디스크에서 전체 도큐먼트를 읽고 쓴다. 갱신은 전체 도큐먼트를 다시 쓰며, 원자성 갱신은 도큐먼트 단위로 실행된다

* 쿼리 및 쓰기의 접근 패턴 : 스키마 설계의 우선 순위를 가장 일반적인 쿼리에 지정할때 최상의 성능을 가진다. 쿼리 수를 최소화하고 함께 쿼리되는 데이터가 동일한 도큐먼트에 저장되도록 하는것이 가장 좋다. 예를 들어, 읽기가 빈번한 경우 데이터 중복(denormalization)을 통해 읽기 성능을 향상시킬 수 있다. 

  * 임베딩

    * **장점:**

      * 단일 쿼리로 관련 데이터를 모두 조회할 수 있어 성능 향상.
      * 데이터 일관성 유지가 용이.

      **단점:**

      * 문서 크기가 커질 수 있어 관리가 어려워질 수 있음.
      * 데이터 중복 발생 가능.

  * 레퍼런싱 (Referencing

    * **장점:**

      - 데이터 중복 최소화.
      - 데이터 업데이트 시 일관성 유지 용이.

      **단점:**

      - 여러 번의 쿼리가 필요하여 성능 저하 가능.
      - 조인이 필요할 수 있어 복잡성 증가.

      **사용 사례:**

      - 사용자와 주문 데이터를 별도의 컬렉션으로 관리할 때, 주문 문서에 사용자 ID를 참조.

* 관계 유형 : 도큐먼트를 내장하거나 참조할 방법을 결정한다. 추가로 쿼리하지 않고 도큐먼트를 참조하는 방법을 파악해야 하며, 관계가 변경될 때 갱신되는 도큐먼트 개수를 알아야 한다. 

* 카디널리티 : 현재 관계가 일대일, 일대다, 다대다, 일대 수백만 일지 등을 고려한다. 이런 문제를 고민하면 도큐먼트 간 데이터를 비정규화 해야하는지 여부와 내장, 참조 할지 여부를 결정할 수 있다.

* 인덱스 전략

  * **적절한 인덱스 생성:**

    - 쿼리 성능을 향상시키기 위해 자주 사용하는 필드에 인덱스를 생성합니다.
    - 복합 인덱스(composite index)를 활용하여 여러 필드를 동시에 인덱싱할 수 있습니다.

    **인덱스의 비용 고려:**

    - 인덱스는 읽기 성능을 향상시키지만, 쓰기 작업 시 추가적인 비용이 발생합니다.
    - 필요 없는 인덱스는 생성하지 않도록 주의합니다.

    **인덱스 유형 활용:**

    - **단일 필드 인덱스:** 하나의 필드에 대해 인덱스 생성.
    - **복합 인덱스:** 여러 필드를 결합하여 인덱스 생성.
    - **텍스트 인덱스:** 텍스트 검색을 위한 인덱스 생성.
    - **해시 인덱스:** 특정 해시 기반 쿼리에 최적화된 인덱스.

### 9.1.1 스키마 설계 패턴

**다형성 패턴**

모든 도큐먼트가 유사하지만 동일하지 않은 구조를 가질때 적합하다. 

각 문서는 공통 필드와 함께 유형에 따라 추가적인 필드를 가질 수 있습니다.

**사용 사례:**

- 다양한 종류의 사용자 활동 로그를 하나의 컬렉션에 저장할 때.
- 여러 유형의 콘텐츠(예: 게시글, 댓글, 이미지)를 단일 컬렉션에 저장할 때.
- 다양한 타입의 사용자(예: 관리자, 일반 사용자)를 하나의 컬렉션에 저장할 때.
- 여러 종류의 로그(예: 오류 로그, 접근 로그)를 한 컬렉션에 저장할 때.

**장점:**

- 단일 컬렉션으로 관리가 용이.
- 다양한 유형의 데이터를 유연하게 저장 가능.
- 공통 필드를 공유하여 일관성 유지.

**단점:**

- 데이터 유형이 많아지면 문서 구조가 복잡해질 수 있음.
- 특정 유형의 데이터에 대한 쿼리가 복잡해질 수 있음.

**속성 패턴**

문서 내에 다양한 속성을 동적으로 추가하여 데이터를 저장하는 패턴입니다. 스키마가 고정되어 있지 않고, 필요에 따라 속성이 추가됌

정렬하거나 쿼리하려는 공통 특성을 갖는 도큐먼트에 필드의 서브셋이 있는 경우, 정렬하려는 필드가 도큐먼트의 서브셋에만 존재하는 경우에 적합하다. 



**사용 사례:**

- 사용자 정의 필드가 자주 변경되거나 다양하게 사용되는 경우. -> 사용자 설정 및 환경 설정.
- 유연한 데이터 모델이 필요한 애플리케이션.
- 제품의 다양한 속성 관리.

**장점:**

- 높은 유연성.
- 애플리케이션 요구사항에 따라 쉽게 확장 가능.

**단점:**

- 데이터 무결성 관리가 어려울 수 있음.
- 복잡한 쿼리가 필요할 수 있음.

**버킷 패턴**

데이터가 일정 기간 스트림으로 유입되는 시계열 데이터에 적합하다. 

관련된 여러 개의 작은 문서를 하나의 큰 문서(버킷)으로 묶어 저장하는 패턴. 주로 시간 기반 데이터나 이벤트 로그에 사용.

**장점:**

- 쿼리 성능 향상 (한 번의 조회로 여러 데이터를 가져올 수 있음). 문서 수를 줄여 성능 향상.
- 디스크 I/O 감소.
- 관련 데이터를 한 번에 조회 가능 

**단점:**

- 문서 크기 관리 필요 (16MB 제한).
- 데이터 삽입 시 버킷 관리 로직이 필요함.

**이상치 패턴(outlier)**

인기도가 중요한 상황을 위해 설계된 스키마 패턴으로, 소셜 네트워크 등과 같은 곳에서 볼 수 있다.

flag를 사용해 도큐먼트가 이상점일을 나타내며 추가 데이터를 하나 이상의 도큐먼트에 저장한다

**사용 사례:**

- 로그 데이터 중 오류 로그를 별도로 저장할 때.
- 일반 사용자 데이터와 VIP 사용자 데이터를 분리하여 저장할 때.

**장점:**

- 특이 데이터의 관리 용이.
- 일반 데이터의 성능 최적화.

**단점:**

- 데이터 분산으로 인해 관리 복잡성 증가.
- 특이 데이터를 조회할 때 별도의 쿼리가 필요.

**계산된 패턴** (카운티드)

데이터가 자주 계산해야 할 때나 데이터 접근 패턴이 읽기 집약적 일때 사용한다. 주요 도큐먼트가 주기적으로 갱신되는 백그라운드서 계산을 수행하도록 권장한다. 

문서 내에 특정 조건을 만족하는 데이터의 개수를 미리 계산하여 저장하는 패턴입니다. 주로 집계 결과를 빠르게 조회하기 위해 사용됩니다.

**사용 사례:**

- 블로그 게시물의 댓글 수 저장.
- 상품의 리뷰 개수 저장.

**장점:**

- 빠른 조회 성능.
- 실시간 집계 필요 없음.

**단점:**

- 데이터 일관성 유지가 필요함 (쓰기 시 카운트 업데이트).
- 동시 업데이트 시 일관성 문제 발생 가능.
- 카운트 값의 정확성 관리 필요.

**서브셋 패턴**

장비의 램 용량을 초과하는 작업 셋이 있을 때 사용한다. 자주 사용하는 데이터와 자주 사용하지 않는 데이터를 두개의 개별 컬렉션으로 분할해서 사용한다 ( 전자상거래 앱 처럼)

**정의:** 문서 내에 관련된 데이터의 일부 집합(subset)을 포함시키는 패턴입니다. 주로 필요한 데이터만을 포함하여 문서 크기를 줄이기 위해 사용됩니다.

**사용 사례:**

- 사용자 프로필에서 최근 활동 기록만 저장.
- 제품 정보에서 인기 있는 속성만 저장.

**장점:**

- 문서 크기 감소.
- 필요한 데이터만 빠르게 조회 가능.

**단점:**

- 전체 데이터를 조회할 때 여러 문서를 조합해야 할 수 있음.
- 데이터 일관성 관리가 어려울 수 있음.

**확장된 참조 패턴**  

**정의:** 기본적인 참조 패턴을 확장하여, 참조된 문서의 일부 데이터를 함께 저장하거나 인덱싱하는 패턴입니다.

**사용 사례:**

- 사용자의 기본 정보와 함께 자주 조회되는 일부 프로필 데이터를 함께 저장.
- 주문 정보와 함께 상품의 가격 정보를 함께 저장.

**장점:**

- 조인 없이도 필요한 데이터 일부를 빠르게 조회 가능.
- 읽기 성능 향상.

**단점:**

- 데이터 중복으로 인한 일관성 유지 필요.
- 쓰기 시 추가적인 업데이트 작업 필요.

**근사 패턴** 



**트리 패턴** 

**정의:** 계층적 데이터를 트리 구조로 저장하는 패턴입니다. 부모-자식 관계를 표현하며, 여러 방식(예: 부모 참조, 자식 임베딩, 경로 저장)으로 구현할 수 있습니다.

**사용 사례:**

- 카테고리 트리, 조직도, 파일 시스템 구조 저장.
- 댓글 및 응답의 계층적 관계 저장.

**장점:**

- 계층적 관계를 자연스럽게 표현 가능.
- 다양한 트리 구조를 유연하게 저장.

**단점:**

- 깊은 트리 구조는 쿼리 성능 저하 가능.
- 데이터 일관성 유지가 어려울 수 있음.



**도큐먼트 버전 관리 패턴**  

도큐먼트에 부가 필드를 추가하여 버전을 관리한다



몽고디비 데이터 모델링 사이트 : 

* https://learn.mongodb.com/catalog
* https://www.mongodb.com/blog/post/building-with-patterns-a-summary

## 9.2 정규화 vs 비정규화 

정규화는 컬렉션간 참조를 이용해 데이터를 여러 컬렉션으로 나누는 작업이다.

몽고 DB 집계 프레임워크는 left outer join을 수행하는 `$lookup` 단계에서 제공한다. 

* 샤딩된 클러스터에서도 `\$lookup`을 사용하여 조인 연산을 수행할 수 있다.
* 그러나 조인하려는 필드가 공통 샤드키가 아닌 경우 룩업은 브로드캐스트로 동작해서 성능 저하가 발생할 수 있다. 모든 샤드에서 데이터를 조회하고 결합해야 하기 때문이다.  
* 예를 들어, 사용자와 주문을 자주 조인하는 경우, 두 컬렉션 모두 `user_id`를 샤드 키로 설정하는 것이 좋다. 

비정규화는 모든 데이터를 하나의 도큐먼트에 내장하여 의도적으로 중복을 허용한다. 정보가 변경되면 여러 도큐먼트가 갱신되어야 한다. 

정규화는 쓰기를 빠르게, 비정규화는 읽기를 빠르게 만든다. 

### 9.2.2 카디널리티

카디널리티는 컬렉션이 다른 컬렉션을 얼마나 참조하는지 나타내는 개념이다.

몽고디비 사용시 다수라는 개념을 많음과 적음으로 나타내면 편리하다.

게시물을 조금만 작성하면 유저-게시물은 일대 소, 태그보다 게시물이 많으면 게시물과 태그는 다대소 등이다.

적음 관계는 내장이 적합하고, 많음 관계는 참조가 더 적합하다 

### 9.2.3 친구, 팔로워, 관계

서로 친구끼리 구독하는 방법은 3가지가 있다.



1. 게시자를 구독자의 도큐먼트에 넣는법

```json
{
  "following" : [
    ObjectId(),
    ObjectId(),
    
  ]
}
```

다음처럼 쿼리해서 사용자가 관심 가질 수 있는 활동을 모두 찾을 수 있다.

```
db.activieis.find({"user" : {"$in" : user["following"]}})
```

하지만 새로 게시된 활동에 관심있는 사람을 모두 찾으려면 반대로 모든 사용자에 걸쳐 following을 쿼리해야 한다

2. 게시자 도큐먼트에 팔로워 추가

```json
{
  "followers" : [
    ObjectId(),
    ObjectId(),
  ]
}
```

뭔가 할때마다 알림 보낼시 전체 보낼 수 있지만, 팔로우 하는 사람을 모두 찾으려면  users 컬렉션 전체를 쿼리해야 한다



마지막 옵션은 정규화하고 구독을 다른 컬렉션에 저장한다

게시자와 구독자를 짝지어서 컬렉션에 저장한다

```
{
  "_id" : ObjectID // 팔로우 대상의 id
  "followers" : [
    ObjectId(),
    ObjectId(),
  ]
}
```



#### 유명인 사용자로 인한 영향 대처하기

예를들어 팔로워가 백만 천만이 넘어 한 컬렉션에서 저장할 수 있는 데이터를 넘긴다면?

연속 도큐먼트를 사용해 처리할 수 있다.

wil이라는 유저에다가

```json
{
  "username" : "wil",
  "tbc" : [ // 투비 컨티뉴 배열 
    ObjectId() // 다음 도큐먼트 아이디,
    ObjectId() // 다음 도큐먼트 아이디,
    
  ]
}
```



## 9.5 일관성 관리

몽고디비는 각 연결에 대한 요청 큐를 보관하고 요청은 큐의 맨뒤에 쌓인다.

여러 스레드에서 동시 요청을 보낼시, 먼저 요청이 데이터를 저장하고 뒤 요청이 데이터를 읽을때 데이터가 없는것처럼 보일 수 있다. 이러한 동작은 커넥션 풀을 사용하는 드라이버에서 주의해야 한다.

읽기 요청을 레플리카 셋의 세컨더리로 보낼 때 이는 큰 문제가 될 수 있다. 

만약 최신의 데이터만을 바로 읽고싶다면 프라이머리 노드에 읽기 요청을 보내면 됀다.

몽고디비는 일관성과 격리 속성을 제어하는 readConcern 옵션을 제공한다.

writeConcern과 결합하면 애플리케이션에 대한일관성과 가용성 보장을 제어할 수 있다. 

local, available, majority, linearizable, snapshot 이라는 수준이 있다.

| **readConcern 수준** | **일관성(consistency)**                  | **격리(isolation)**                        | **사용 사례**                                            |
| -------------------- | ---------------------------------------- | ------------------------------------------ | -------------------------------------------------------- |
| **local**            | 낮음: 최신 데이터 보장하지 않음          | 낮음: 다른 쓰기 작업의 영향을 받을 수 있음 | 빠른 응답이 중요한 애플리케이션                          |
| **available**        | 중간: 일부 일관성 보장                   | 중간: 일부 격리 제공                       | 균형 잡힌 성능과 일관성이 필요한 경우                    |
| **majority**         | 높음: 대다수 노드에 커밋된 데이터만 읽음 | 높음: 높은 격리 수준                       | 금융, 의료 등 데이터 일관성이 중요한 애플리케이션        |
| **linearizable**     | 매우 높음: 항상 최신 데이터 보장         | 매우 높음: 완전한 격리 제공                | 실시간 금융 거래 시스템 등 절대적인 일관성이 필요한 경우 |
| **snapshot**         | 트랜잭션 내 일관된 데이터 스냅샷 제공    | 트랜잭션 동안 높은 격리 수준 제공          | 다중 문서 트랜잭션에서 일관된 읽기 작업 필요 시          |



## 9.8 몽고DB를 사용하지 않는 경우

몽고DB가 대부분의 애플리케이션에 잘 작동하는 범용 데이터베이스긴 하지만 모든 상황에 적 합하지는 않다. 몽고DB는 다음과 같은 사례에는 부적합하다.

- ﻿﻿다양한 유형의 데이터를 여러 차원에 걸쳐 조인하는 작업은 관계형 데이터베이스에 적합하다. 몽고DB는 이러 한 작업을 잘 처리하지 못하며 거의 다룰 일이 없다.
- ﻿﻿몽고DB보다 관계형 데이터베이스를 사용하는 가장 큰 이유는 몽고DB를 지원하지 않는 도구를 사용할 수 있 기 때문이다. SQL 알케미(SQLAchem에서 워드프레스까지. 몽고DB를 지원하지 않는 도구는 수천 개가 있다. 지 원하는 도구가 늘고 있긴 하지만 아직 관계형 데이터베이스 생태계를 따라가지 못한다.



# 10 복제 셋(레플리카 셋)



## 10.1 복제 소개

복제는 데이터의 동일한 복사본을 여러 서버상에서 보관하는 방법이며 실제 운영시 권장된다. 

복제 셋은 요청을 처리하는 프라이머리 서버 한대와, 프라이머리 데이터의 복사본을 갖는 세컨더리 서버 여러대로 이루어진다.

프라이머리에 장애 발생시 세컨더리 서버는 자신들 중 새로운 프로이머리를 선출한다.

## 복제 설정

```
version: '3.8'

services:
  mongo1:
    image: mongo:latest
    container_name: mongo1
    restart: always
    ports:
      - 27017:27017
    volumes:
      - ./data/mongo1:/data/db
    command: mongod --replSet rs0 --bind_ip_all

  mongo2:
    image: mongo:latest
    container_name: mongo2
    restart: always
    ports:
      - 27018:27017
    volumes:
      - ./data/mongo2:/data/db
    command: mongod --replSet rs0 --bind_ip_all

  mongo3:
    image: mongo:latest
    container_name: mongo3
    restart: always
    ports:
      - 27019:27017
    volumes:
      - ./data/mongo3:/data/db
    command: mongod --replSet rs0 --bind_ip_all
```

몽고디비 에 접속하여 레플리카셋을 초기화한다

```
rs.initiate(
  {
    _id: "rs0", // 레플리카셋 아이디. 즉 이름 
    version: 1,
    members: [
      { _id: 0, host: "mongo1:27017" },
      { _id: 1, host: "mongo2:27017" },
      { _id: 2, host: "mongo3:27017" }
    ]
  }
)

초기화가 완료되었는지 확인하려면 다음 명령어를 실행합니다.


rs.status()

정상적으로 초기화되었다면, 모든 멤버가 PRIMARY, SECONDARY 상태로 표시됩니다.
```

다른 명령어는

```
mongodb --bind_ip localhost, 192.51.100.1 --replSet mdbDefGuide \
--dbpath ~/data/rs1 --port 27017 --oplogSize 200
```

## 10.8 복제셋 설계 방법

프라이머리 선출시 과반수 이상이 필요하기 때문에 멤버 수가 중요하다.

복제 셋 내 모든 멤버의 절반보다 많은것으로 정의해야 한다.



### 선출 방식

세컨더리가 프라이머리가 되지 못하면 다른 멤버들에 자신을 프라이머리로 선출할것을 요청한다

* 요청받은 멤버가 프라이머리에 도달할 수 있는가
* 데이터가 최신인가
* 우선순위가 더 높은 멤버는 없는가

## 10.9 멤버 구성 옵션

### 우선순위

우선순위는 0~100 사이 값으로 지정 가능하며 기본값은 1이며, priority가 0이면 그 멤버는 절대 프라이머리가 될 수 없는 수동적 멤버이다.

```
rs.add({"host" : "server-4:27017", "priority" : 1.5})
```

### 아비터 선출

멤버가 2개인 복제 셋은 요구사항이나 명확한 단점이 있다.

대부분 사람들이 3개까지 필요없다고 생각해서 2개로 두는 케이스도 있다.

이때 몽고디비는 프라이머리 선출에 참여하는 용도로만 쓰이는 아비터라는 특수한 멤버를 지원한다. 아비터는 데이터를 가지지 않으며클라이언트에 의해 사용되지 않는다. 2-멤버 복제셋에서 과반수를 구성하는데 사용된다 (바람직하진 암ㅎ음)

# 11 복제 셋 구성 요소

- ﻿﻿복제 셋 멤버가 새로운 데이터를 복제하는 방법
- ﻿﻿새로운 멤버를 영입하는 방법
- ﻿﻿선출이 작동하는 방법
- ﻿﻿발생할 수 있는 서버 및 네트워크 오류 시나리오

## 11.1 동기화

몽고디비는 프라이머리가 수행한 쓰기를 모두 포함하는 로그인 oplog를 보관함으로써 복제를 수행한다

oplog는 프라이머리의 로컬 데이터베이스에 있는 제한 컬렉션이며 세컨더리는 이 컬렉션에 연산을 쿼리한다.

각 세컨더리는 프라이머리로부터 복제한 작업을 각각 기록하는 oplog를 보관한다. 

세컨더리가 다운되면 재시작할떄 oplog에있는 마지막 연산과 동기화 한다. 

oplog는 여러번 실행해도 한번 실행할때와 같은 결과를 얻으며 각 작업은 멱등하다.

oplog는 크기가 고정되어 있어 담을 수 있는 연산의 수가 정해져있다.

쓰기연산이 분당 1kb 발생한다면 oplog는 분당 1킬로바이트씩 채워진다.

연산 하나가 프라이머리에 수행시 영향받는 도큐먼트 개수당 하나씩 oplog 연산으로 분할된다.

oplog는 일반적으로 디폴트 크기면 충분하고, 다음은 oplog 크기가 기본 크기보다 커야할 수 있는 종류다

* 한번에 여러 도큐먼트 갱신 
* 삽입한 데이터와 동일한 양의 데이터 삭제
* 상당 수의 내부 갱신 

### 11.1.1 초기 동기화

**정의**
복제 셋(replica set)의 새로운 멤버가 기존 멤버(동기화 소스)의 모든 데이터를 전부 복사해오는 과정

**절차 요약**

1. 대상 멤버가 다른 멤버로부터 전체 데이터를 복사할 수 있는 **유효한 상태**인지 확인
2. **Local DB**(oplog 등 내부용) 제외, 소스에 있는 모든 데이터베이스 및 컬렉션 전부 스캔 & 복사
3. 데이터 복사가 완료되면 소스의 **oplog**(증분 로그)를 사용해 복사 도중 발생한 변경 사항(삽입/업데이트/삭제)을 적용
4. 최종적으로 세컨더리(secondary) 역할을 할 준비가 완료됨(정상 동기화 단계 전환)

#### 초기 동기화 상세 진행 방식

1. **대상 멤버의 기존 데이터 삭제**
   - 초기 동기화가 시작되면, 대상 멤버 디렉터리 내 **기존 데이터는 전부 삭제**됨
   - **경고**: 따라서 이미 있던 데이터가 필요하지 않은 경우, 혹은 따로 백업해둔 경우에만 초기 동기화를 수행해야 함
2. **MongoDB 3.4 이후 버전의 인덱스 구축 방식**
   - **3.4 이상**: 각 컬렉션의 모든 인덱스(_id 포함)를 **도큐먼트를 복사**해오는 단계에서 **동시에** 생성
   - **3.4 미만**: 도큐먼트 복사 중에는 _id 인덱스만 생성하고, 나머지 인덱스는 나중 단계에 생성
3. **oplog 레코드 보관을 위한 디스크 용량 확인**
   - 데이터 복사 중에도 새로운 쓰기 작업(삽입, 업데이트, 삭제)이 **oplog**에 쌓임
   - 대상 멤버(초기 동기화를 수행하는 쪽)는 복사 단계에서 새로 생기는 oplog 기록을 저장할 **충분한 디스크 공간**이 필요
4. **복사가 끝난 후, oplog 내용을 재적용**
   - 복사가 진행되는 동안 발생했던 모든 변경 사항을 **소스 oplog**를 통해 적용
   - 만약 복사 중 누락된 문서가 있다면 다시 재복제(recopy)하는 과정을 거침
5. **복제 완료 시점**
   - 소스(primary)의 어느 시점과 동일하게 맞춰져야 하며, 그 시점이 맞춰지면 **일반 동기화 단계**(세컨더리 동작)로 전환

#### 초기 동기화 시 고려사항 & 문제점

1. **데이터 크기와 성능 이슈**

   - 초기 동기화 중에는 소스 서버에서 

     모든 데이터를 읽고 전송

     해야 하므로,

     - 네트워크 및 디스크 I/O 부하가 높아지고
     - 소스 서버의 **작업 셋(자주 사용되는 데이터)**이 교체되어 성능이 저하될 수 있음(메모리에서 밀려나 디스크로 향하는 접근 증가)

   - 데이터셋이 작거나 시스템에 여유 자원이 많다면 큰 문제가 안 될 수 있음

2. **초기 동기화 지연 및 oplog 누락 위험**

   - 데이터 양이 많아 **복사 시간이 오래 걸리면**, 새 멤버가 **oplog 끝부분을 따라가지 못할** 수 있음
   - oplog가 회전(오버라이팅)되어 필요한 레코드를 더 이상 저장하지 않으면, 해당 멤버는 정상적으로 동기화를 마칠 수 없음
   - 이를 피하려면 **부하가 적은 시간대**에 진행하거나 **백업으로부터 복원**하는 방법 고려

3. **백업 복원 권장**

   - 초기 동기화를 통해 모든 데이터를 네트워크 넘어 가져오면 시간이 많이 걸릴 수 있음
   - **백업**(예: 물리 스냅샷, mongodump 등)에서 **복원**한 후, oplog만 추가로 동기화하는 방법이 더 빠르고 안정적인 경우가 많음

- **초기 동기화**는 새 멤버가 다른 멤버와 동일한 데이터 상태를 갖추기 위해 수행하는 과정이다.
- **기존 데이터는 전부 삭제**되고, 소스 서버에서 모든 데이터를 복사해온 뒤, 발생한 변경사항(oplog)을 재적용하는 식으로 진행된다.
- 단, 복사 부하가 커서 서버 성능에 부담을 줄 수 있고, oplog 누락 위험도 존재한다. 따라서 **백업 복원** 방식을 활용하거나 **부하가 낮은 시간대**에 진행하는 것이 좋다.
- 실제 운영 환경에서는 초기 동기화보다 **백업으로부터 복원**하는 접근이 더 빠르고 안정적인 경우가 많다.

### 11.1.2 두번째 동기화 유형 복제

초기 동기화 후 지속적으로 비동기적으로 oplog를 복사하여 적용함.

## 11.2 하트비트

멤버는 다른 멤버의 상태정보를 알아야 한다.

멤버는 복제셋에 대한 최신 정보 유지하기 위해 모든 멤버로 2초마다 하트비트 요청을 보낸다. 

### 멤버 상태

* STARTUP : 멤버 처음 시작 상태. 멤버의 복제 셋 구성 정보 로드 시도시 이 상태이며, 구성 정보가 로드되면 STARTUP2로 전환

* STARTUP2 : 초기 동기화 과정에 지속되는 그냥 상태
* RECOVERING : 올바르게 작동하지만 읽기 작업은 수행할 수 없는 상태. 다른 멤버들보다 복제가 뒤쳐질때 따라잡기 위해 이 상태가 되기도 함. 
* ARBITOR : 아비터 선출
* DOWN : 멤버가 살아있지만 도달할 수 없는 상태
* UNKNOWN : 알 수 없는 멤버가 다운되거나 두 멤버간 네트워크 문제가 있을떄
* REMOVE : 복제셋으로부터 제거된 상태
* ROLLBACK : 데이터 롤백시

# 12. 애플리케이션에서 복제 셋 연결

복제 셋이면 기본적으로 드라이버는 프라이머리에 연결되고 모든 트래픽을 프라이머리에 라우팅함.

복제 셋에 대한 연결은 단일 서버와 비슷함. 

```
spring:
  data:
    mongodb:
      uri: mongodb://<username>:<password>@host1:port1,host2:port2,host3:port3/<database>?replicaSet=rs0
```

복제 셋의 목적은 서버가 다운되도 데이터의 가용성을 높이는 것이다. 

프라이머리가 다운되면 드라이버는 자동으로 새 프라이머리를 찾고 가능한 빨리 프라이머리로 요청을 라우팅한다 그러나 프라이머리가 없는 동안 애플리케이션이 쓰기를 수행할 수 없다.



## 세컨더리로 읽기 전송

기본적으로 모든 요청을 프라이머리로 라우팅하는데, read preference를 설정해 다른 옵션을 구성할 수도 있다.

그러나 읽기 요청을 세컨더리에 보내면 좋지 않다. 

일관된 읽기를 보장하지 않을 수 있기 때문이다.

* MongoDB의 세컨더리 노드는 리플리케이션을 통해 프라이머리 노드에서 데이터를 복제하지만, 항상 최신 상태를 보장하지는 않음

* 세컨더리 노드는 보통 데이터 백업, 분석 쿼리, 또는 고가용성을 위해 유지됩니다.

  세컨더리에 너무 많은 읽기 요청이 집중되면 리플리케이션 프로세스가 느려지고, 데이터 복제가 지연될 수 있음. 

  * 세컨더리에서 읽는 도중 프라이머리에서 새로운 쓰기가 발생하면, 리플리케이션이 완료되기 전에 읽기 요청이 처리될 수 있음

부하를 분산하려고 읽기를 세컨더리로 보내고 싶다면 샤딩을 구선하는것이 더좋다. 



세컨더리로부터 읽기 관련해서 지연율이 낮은 읽기가 중요하다. 

어느 정도의 읽기 요청이 프라이머리로부터 발생한다면 primary를 사용한다. 

다른 읽기는 데 이터가 최신이 아니어도 괜찮다면 primaryPreferred를 사용한다. 

그리고 요청에 일관성보다 는 낮은 지연율이 필요하다면 nearest를 사용한다.



# 13장 관리

* 개별 멤버에 유지보수 수행
* 다양한 환경에서 복제 셋 구성
* 사용자 oplog 정보를 얻고 크기 조정
* 색다른 복재 셋 구성
* 마스터 슬레이부 구조에서 복제셋으로 전환



# 14장 샤딩

샤딩은 여러 장비에 걸쳐 데이터를 분할하는 과정. 때때로 파티셔닝 이라고도 불림

몽고디비는 자동 샤딩을 지원한다. 



### 14.1.1 클러스터 구성 요소 이해하기

샤딩을 통해 많은 장비의 클러스터를 생성하고 각 샤드에 데이터 서브셋을 넣어 컬렉션을 쪼갤 수 있다.

샤딩은 하나의 장비처럼 보이게 mongos라는 라우팅 프로세스가 도와준다.

<img src="./images//image-20241227195339443.png" width = 350 height = 400>

## 단일 장비 클러스터에서의 샤딩

```
mongo --nodb --norc
```

몽고디비는 데이터를 분산할 방법을 모르기 때문에 컬렉션마다 데이터를 어떻게 분산할지 알려야 한다

특정 컬렉션을 샤딩하려면 먼저 컬렉션의 데이터베이스에서 샤딩을 활성화 한다

```
db.users.createIndex({"username" : 1})
sh.enableSharding("accounts")
```

컬렉션 샤딩시 샤드 키를 선택하는데, 이는 몽고디비가 데이터 분할시 선택하는 필드다. 

컬렉션이 커질수록 샤드 키가 컬렉션에서 가장 중요한 인덱스가 된다. 

샤드 키를 만들려면 필드에 인덱스를 생성해야 한다. 

쿼리는 모든 데이터를 찾으려면 모든 샤드를 방문해야 하며, 일반적으로 쿼리에서 샤드 키를 사용하지 않으면 mongos는 모든 샤드에 쿼리를 보내야 한다.

* 샤드 키를 포함하며 단일 샤드나 샤드 서브셋으로 보내는 쿼리 : 타겟 쿼리
* 모든 샤드로 보내야 하는 쿼리 : 분산-수집 쿼리

# 15 샤딩 구성

## 언제 샤딩해야 하나

일반적으로 샤딩은 다음과 같은 경우에 사용된다.

- ﻿﻿사용 가능한 메모리를 늘릴 때
- ﻿﻿사용 가능한 디스크 공간을 늘릴 때
- ﻿﻿서버의 부하를 줄일 때
- ﻿﻿한 개의 mongod가 다룰 수 있는 처리량보다 더 많이 데이터를 읽거나 쓸 때

### 구성 서버(config sever)

구성 서버는 클러스터의 두뇌로 어떤 서버가 무슨 데이터를 갖고있는지에 대한 모든 메타 데이터를 들고 있따

그러므로 가장 먼저 구성 서버를 설정해야 한다.

**역할**

1. **메타데이터 저장**
   - 각 샤드에 어떤 데이터가 저장되어 있는지에 대한 **분배 정보**를 저장합니다.
   - 데이터의 **샤드 키(shard key)**와 관련된 분포, 청크(chunk) 위치 등을 관리합니다.
   - 클라이언트 요청이 올바른 샤드로 라우팅될 수 있도록 메타데이터를 제공합니다.
2. **쿼리 라우팅 지원**
   - 쿼리를 처리하는 **몽고S(MongoS)** 라우터가 구성 서버에서 메타데이터를 읽어와 클라이언트의 요청을 적절한 샤드로 라우팅합니다.
   - 이 과정에서 데이터가 어디에 위치하는지 알 수 있도록 구성 서버가 중요한 역할을 합니다.
3. **클러스터 상태 관리**
   - 샤드 클러스터 전체의 상태 정보를 유지합니다.
   - 샤드 분할, 병합, 이동과 같은 작업에서 중심적인 역할을 합니다.

**복제 셋(Replica Set) 사용**

- MongoDB 3.4 이후, 구성 서버는 고가용성과 안정성을 위해 **복제 셋(replica set)**으로 구성됩니다.
- 최소 3개의 노드를 운영하는 것이 권장되며, 이로 인해 구성 서버 장애로 인한 클러스터 다운타임을 방지합니다.

**독립적인 서버**

- 구성 서버는 데이터베이스 클러스터의 다른 노드와 독립적으로 구성되어야 하며, 애플리케이션 데이터를 저장하지 않습니다.



구성서버를 설정하고 나면 mongos 프로세스를 시작해야 한다

```
mongos --configdb\
configRS/cfg1.example.net:27019, \
cf2.example.net:27019,cfg3.example.net:27019 \
--bind_ip localhost:198.51.100.100 --logpath /var/log/mongos.log
```

### 복제셋으로부터 샤딩 추가

기존 복제셋이 있는 경우

- 이미 복제셋이 있다면 해당 셋이 첫번째 샤드
- mongos에게 샤드를 구성할 복제 셋을 찾는 방법을 알려야 한다



### 데이터 샤딩

분산하려는 데이터베이스와 컬렉션을 명시적으로 알려줘야한다

우선 데이터베이스를 샤딩을 하고 컬렉션을 샤딩한다

```
sh.enableSharding("music")

sh.shardCollection("music.artists", {"name" : 1})
```



## 15.3 몽고디비는 어떻게 클러스터 데이터를 추적하는가

mongos는 샤드키가 주어지면 도큐먼트를 어디서 찾을지 알아야 한다.

몽고 디비는 주어진 샤드 키 범위 내에 있는 도큐먼트를 청크로 나눈다

하나의 청크는 항상 하나의 샤드에 위치한다

* 사용자 컬렉션의 샤드 키가 age : 1 이면 하나의 청크는 age 가 3~17인 사이인 모든 도큐먼트 
* age 5 쿼리 요청을 받으면 청크 3 17 사이에 있는 샤드에 쿼리를 라우팅 한다

도큐먼트는 항상 단 하나의 청크에만 속하기 때문에 배열 필드는 샤드 키로 사용할 수 없다

청크가 커지면 몽고 디비는 자동으로 음 무한대에서 양의 무한대까지 범위를 갖는 두개의 청크로 나눈다

청크 정보는 config.cunks 컬렉션에 저장된다.

복합 샤드키의 경우 샤드 범위는 두개의 키로 정렬할 때와 동일한 방식으로 작동한다

{username : 1, age : 1} 에 샤드키가 있다면 적합하게 나눈다.

### 청크 분할

각 샤드 프라이머리 mongod는 청크에 얼마나 많은 데이터가 삽입됐는지 추적하고 특정 임계치에 이르면 청크가 나뉘어야 할지 확인한다.

그 다음 청크 분할 수행하고 메타데이터를 갱신한다. 

샤드 키가 같은 두 도큐먼트는 같은 청크에 있어야 하기 때문에 청크는 샤드 키 값이 변경되는 도큐먼트를 기준으로만 분할될 수 있다.

## 밸런서

밸런서는 데이터 이동을 책임진다.

주기적으로 샤드 간의 불균형을 체크하다 불균형이면 청크를 이동하기 시작한다.

밸런서는 구성 서버 복제 셋의 프라이머리 멤버에 있다.

백그라운드 프로세스며 샤드의 청크 수가 임계치에 이를때만 활성화 된다.

애플리케이션은 데이터가옮겨다닌다는 사실을 몰라도 되며, 데이터 이동이 완료될때까지 모든 읽기와 쓰기는 이전 청크로 전달된다.



# 샤드 키 선정

컬렉션 샤딩시 데이터 분할에 사용할 한두 개의 필드를 선택한다

컬렉션을 샤딩하고 나서는 샤드 키를 변경할 수 없다.

먼저 샤딩할 각 컬렉션에 대해 다음 질문에 답해보자.

- ﻿﻿샤드를 얼마나 많이 늘릴 것인가?
   샤드가 세 개인 클러스터는 샤드가 천 개인 클러스터보다 훨씬 유연하다. 클러스터가 점점 더 커질 때, 쿼리가 모든 샤드를 방문해야 하는 쿼리를 피하려면 거의 모든 쿼리가 샤드 키를 포함해야 한다.
- ﻿﻿읽기 혹은 쓰기 응답 대기 시간을 줄이려고 사당하는가?
   (응답 대기 시간은 무언가를 얻는 데 걸리는 시간을 일컫는다. 예를 들어 쓰기는 20밀리초가 걸리지만 10밀 리초로 줄일 필요가 있다) 쓰기 응답 대기 시간을 줄이는 일은 일반적으로 요청을 지리적으로 더 가까운 곳이 나 더 좋은 장비로 보내는 작업과 관련 있다.
- ﻿﻿읽기 혹은 쓰기 처리량을 늘리려고 샤당하는가?
   (처리량은 클러스터가 동시에 처리할 수 있는 요청의 개수를 일컫는다. 예를 들어 클러스터는 20밀리초 안에 천 개의 쓰기를 처리하지만 20밀리초 안에 5천 개의 쓰기를 처리할 필요가 있다) 처리량을 늘리는 일은, 더 많은 병렬 처리 기능oarallization을 추가하고 클러스터에 요청을 균일하게 분산하는 작업과 관련 있다.
- ﻿﻿시스템 리소스를 늘리려고 샤딩하는가?
   예를 들어 데이터의 1기가바이트당 몽고DB에 더 많은 메모리를 제공하려고 하는가? 만약 그렇다면 작업 셋 의 크기를 가능한 한 작게 유지할 필요가 있다.



- ﻿﻿샤드 키가 필요한 타겟 쿼리를 제공하는가?
- ﻿﻿샤드 키가 시스템의 처리량이나 응답 대기 시간을 의도한 대로 변경하는가?
- ﻿﻿작은 작업 셋이 필요하면 샤드 키가 그것을 제공하는가?



## 샤딩 구상

데이터 분할시 오름차순, 무작위, 위치 기반 키를 가장 일반적으로 사용한다



### 오름차순 샤드 키

date 필드나 objectId처럼 시간에 따라 증가하는것이면 무엇이든 된다

새 도큐먼트 생성시 maxKey 청크에 들어가게 된다.

이경우 모든 쓰기가 하나의 샤드로 몰리며, 청크의 끝부분에서 새 청크들이 떨어져 나온다

이 패턴은 몽고 DB가 청크를 균형 잡힌 상태로 유지하기 어렵다. 그래서 몽고DB는 지속적으로 청크를 다른 샤드로 옮겨야 한다

### 무작위 분샨 사드 키

사용자명, 이메일주소, UUID, MD5 해시 등 고유하지 않은 키는 무작위 분샨 샤드키가 될 수 있다.

쓰기가 무작위로 분산되므로 발생할 수 있는 이동횟수를 제한하면서 각 샤드가 거의 비슷한 비율로 커진다

유일한 단점은, 메모리 크기를 넘어서는 데이터를 임의로 접근하는데 효율적이지 않다. 

* 무작위로 데이터를 분산시키면 동일한 카테고리나 범주의 데이터를 특정 샤드에 묶을 수 없습니다.

* 예를 들어, 특정 사용자와 관련된 모든 데이터를 같은 샤드에 저장해야 효율적인 쿼리를 처리할 수 있는 경우, 무작위 분산은 데이터 조회에 불리합니다.

* 관련 데이터가 여러 샤드에 분산되어 있어, 쿼리 시 **모든 샤드에 브로드캐스트 쿼리**가 발생할 가능성이 높습니다.

### 위치 기반 샤드 키

사용자 IP, 경도 위도, 주소 등

위치는 데이터를 그룹화 하는 추상적인 방법

## 샤드 키 전략

### 해시 샤드 키

데이터를 가능한 빠르게 로드하려면 해시 샤드 키가 최선의 선택이다

해시 샤드 키는 어떤 필드라도 무작위로 분산한다.

많은 쿼리에서 오름차순 키를 사용하지만, 쓰기는 무작위로 분산하려고 할 때 해시 샤드 키가 좋다

단점

* 해시 샤드 키는 범위 쿼리가 불가능

해시 인덱스를 사용하고, 컬렉션을 샤딩한다

### GridFS를 위한 해시 샤드키

**GridFS**는 MongoDB에서 대용량 파일(예: 이미지, 동영상, 오디오 파일 등)과 같은 바이너리 데이터를 저장하고 검색하기 위한 파일 스토리지 시스템

GridFS 컬렉션은 샤딩 후보로 적합하지만 fs.cunks에 자동으로 생성된 두가지 인덱스는 좋은 샤드 키가 아니다

files_id에 해시 인덱스를 생성하여 각 파일을 클러스터에 무작위로 분산하고 하나의 파일을 항상 하나의 청크에 둔다.



### 다중 핫스팟

쓰기가 클러스터에 고르게 분산되고 샤드 내에서 오름차순이 되도록 한다.

복합 샤드 키를 사용해 수행한다.

첫번째 값은 카디널리티가 낮은 값이고 두번째 값을 오름차순 값으로 둔다



### **적합한 샤드 키를 선택하는 방법**

#### 1. **균등한 데이터 분배**

- **핫스팟(Hotspot)**을 방지하려면 샤드 키를 기반으로 데이터가 샤드에 고르게 분배되어야 합니다.

- 해시 샤딩(Hash Sharding)

  :

  - 샤드 키를 해싱하여 데이터를 분산시킴으로써 특정 샤드에 데이터가 몰리지 않도록 합니다.
  - 예: `hashed` 키를 사용하면 고르게 분포된 데이터 분배를 보장합니다.

- 고유하거나 다양성이 높은 필드

  를 선택:

  - 샤드 키 값의 고유도가 낮으면 특정 샤드로 데이터가 집중될 위험이 있습니다.
  - 예: `userId`와 같은 고유 필드 사용.

#### 2. **쿼리 패턴 고려**

- 주요 쿼리에 샤드 키 포함

  :

  - 샤드 키가 쿼리에 포함되지 않으면 **스캐터-개더(Scatter-Gather)**가 발생하여 모든 샤드에서 데이터를 검색해야 합니다.
  - 이는 성능 저하로 이어질 수 있습니다.
  - 예: 사용자 기반 데이터를 조회한다면, `userId`와 같은 필드를 샤드 키로 설정.

- 범위 기반 쿼리 최적화

  :

  - 특정 범위로 데이터를 조회하는 경우 범위 쿼리를 지원하는 샤드 키 선택.
  - 예: `date` 필드를 샤드 키로 설정하면 특정 기간의 데이터를 빠르게 조회 가능.

#### 3. **데이터 로컬리티(Locality)**

- 연관된 데이터가 같은 샤드에 저장되도록 설계하면 효율적인 쿼리가 가능.
- 예: 같은 사용자의 데이터를 한 샤드에 저장하려면 `userId`를 샤드 키로 선택.

#### 4. **샤드 키 크기와 인덱스 최적화**

- 샤드 키 필드는 크기가 작고 고유한 값을 가져야 합니다.
- 샤드 키는 기본적으로 인덱스로 생성되므로, 자주 사용되는 필드를 선택하면 쿼리 성능이 향상됩니다.

#### 5. **쓰기 패턴 고려**

- 쓰기 작업이 특정 샤드에 집중되지 않도록 샤드 키를 설계.
- 예: `timestamp` 필드를 샤드 키로 선택하면 쓰기가 최신 샤드로만 몰릴 수 있으므로 해싱을 고려해야 합니다.

#### 6. **필드 변경 가능성**

- 샤드 키로 선택한 필드는 변경이 불가능하므로, 자주 업데이트되지 않는 필드를 선택해야 합니다.

#### 7. **청크 분할과 이동 관리**

- 샤드 키는 MongoDB의 **청크 분할** 및 **이동**을 최적화할 수 있어야 합니다.
- 데이터가 특정 샤드에 몰리는 경우 청크 이동이 빈번하게 발생할 수 있으므로 주의해야 합니다.

------

### **샤드 키로 적합한 필드**

다음은 샤드 키로 적합한 필드의 특징과 예제입니다:

| 필드 유형                        | 적합 여부 | 이유                                                         |
| -------------------------------- | --------- | ------------------------------------------------------------ |
| **고유한 ID (e.g., _id)**        | 좋음      | 데이터가 고르게 분산되며, 단일 키로 데이터를 조회할 때 효율적. |
| **해시된 필드 (e.g., hashed)**   | 좋음      | 데이터를 무작위로 분산하여 핫스팟을 방지.                    |
| **타임스탬프 (e.g., createdAt)** | 조건부    | 범위 쿼리에는 적합하지만, 특정 시점에 쓰기가 몰릴 가능성이 있음. |
| **유형 필드 (e.g., category)**   | 조건부    | 값의 다양성이 낮다면 특정 샤드에 데이터가 몰릴 위험이 있음.  |
| **사용자 ID (e.g., userId)**     | 좋음      | 사용자 데이터를 효율적으로 분산하고 로컬리티를 제공.         |



## 샤드키 제약조건



샤드 키는 배열이 될 수 없고 입력 후 수정할 수 없다. 위치 인덱스도 샤딩할 수 없다.

샤드키는 카디널리티가 높은 필드에 좀더 효율적이다 값이 적을수록 좋지 않다.

변화(다양성)가 거 의 없는 키를 샤드 키로 사용하려면 "logLevel과 "timestamp"처럼 더욱 다양성 있는 키와 함께 복합 샤드 키를 생성해서 사용하자

## 데이터 분산 제어

컬렉션을 모든 샤드에 균등하게 분산하는데, 가치가 낮은 로그 컬렉션을 특정 샤드에 몰아두고 싶다면?

```
sh.addShardToZone("샤드명", "low")
```

수동으로 샤딩하고 싶으면 밸런서를 끄고 moveChunk 명령으로 수동으로 분산이 가능하다

```
st.stopBalancer();

// 이후 확인
use config
while(sh.isBalancerRunning()) {
  sleep(1000);	
}

그다음

db.chunks.find(); // 어떤 청크가 어디에 있는지 확인

sh.moveCunk(
  "test.maual.stuff",
  {user_id: NumberLong()},
  "test-rs1"
)

```



# 샤딩 관리



- ﻿﻿멤버가 누구인지, 데이터가 어디에 보관됐는지, 어떤 연결이 열려 있는지 등 클러스터 상태 검사
- ﻿﻿클러스터 멤버 추가, 제거, 변경
- ﻿데이터 이동 관리 및 데이터 수동 이동



## sh.status()

sh.status()는 샤드, 디비, 샤딩된 컬렉션의 개요를 제공한다. 청크가 어디에 위치하는지도 출력한다

## 구성 정보 확인

```
use config
```



config.shards 는 클러스터 내 모든 샤드를 추적한다. 

config.databases는 클러스터가 알고있는 모든 샤딩 및 비샤딩 데이터베이스를 추적한다

config.collections는 모든 샤딩된 컬렉션을 추적한다.



## 네트워크 연결 추적

db.adminCommand({"connpoolStats" : 1})

샤드 클러스터나 레플리카 셋의 멤버로 나가는 열린 연결에 대한 모든 정보를 반환한다.



mongos는 기본적으로 최대 6만5536개의 연결을 허용한다.

mongos 프로세스가 여러개면 더 많은 연결을 생성할 수 있다.

## 17.3 서버 관리

언제든지 새로운 mongos 프로세스를 추가할 수 있다.

샤드를 추가하려면 addShard 명령을 사용한다

샤드의 멤버구성을 변경하려면 샤드의 프라이머리에 직접 연결해 복제 셋을 재구성해야한다.

수동으로 config.shards를 수정하지 말자.

### 샤드 제거

일반적으로 샤드는 클러스터에서 제거되면 안된다.

필요 이상으로 시스템에 무리를 준다. 

removeShard명령은 샤드명을 가져와서 샤드의 모든 청크를 다른 샤드로 배출한다.

## 데이터 밸런싱

몽고 DB는 자동으로 데이터를 균등하게 분산한다.

### 밸런서

밸런서를 끄면 새로운 밸런싱 작업이 시작되지 않는다.

```
sh.setBalancerState(false)
```

밸런싱 진행중엔 강제로 중지되지 않으므로 확인하고 끈다.

마이그레이션이 문제를 일으킬 수 있는 상황은 두가지 상황이다

1. ﻿﻿﻿핫스팟 샤드 키를 사용하면 지속적으로 마이그레이션이 일어난다(새로운 청크가 모두 핫스팟에 생성되므로).
    시스템은 핫스팟 샤드에서 발생하는 데이터 흐름을 처리할 수 있어야 한다.
2. ﻿﻿﻿새로운 샤드를 추가하면 밸런서가 해당 샤드를 채우려고 시도할 때 지속적인 마이그레이션이 발생한다.

### 청크 크기 변경

기본적으로 청크 크기는ㄴ 64MB이다.

청크 크기를 줄여서 속도를 높일 수 있다.

```
db.settings.save({"id" : "chunksize", "value" : 32})
```



# 애플리케이션 관리



## 현재 작업 확인

애플리케이션을 느려지게하는 요소를 확인하기 위해 실행중인 작업을 화인할 수 있다

```
db.currentOp()
```

## 시스템 프로파일러

시스템 프로파일러로 느린 작업을 system.profile 컬렉션에 기록할 수 있다.

다만 전반적인 몽고디비의 성능을 느려지게 한다.

```
db.setProfilingLevel(2)
```

* 모든것을 프로파일링하겠단 의미 

## 크기 계산

### 도큐먼트 크기 계산

```
Object.bsonsize(db.users.findOne())
```

### 컬렉션

```
db.movies.stats();
```

# 인증과 권한

내장된 역할을 여러개 제공한다

* read
* readwrite
* dbAdmin
* userAdmin
* dbOwner
* clusterManager
* clusterMonitor
* hostManager
* clusterAdmin
* backup
* root



# 영속성



## 저널링을 통한 멤버 수준의 영속성

서버 오류발생시 영속성을 제공하기 위해 몽고디비는 journal이라는 로그 선행 기입 (write-ahead-log) WAL을 사용한다.

몽고디비는 작업로그 혹은 oplog를 기반으로 하는 명령문 기반 복제를 사용한다.

* oplog는 멱등.

 기본적으로 저널 항목을 50밀리초마다 디스크로 플러시하고 데 이터베이스 파일을 60초마다 디스크로 플러시한다. 데이터 파일을 플러시하는 60초 간격을 체 크포인트(heckpon라고 한다. 

저널은 마지막 체크포인트 이후 기록된 데이터의 영속성을 제공하 는 데 사용된다. 

영속성 문제와 관련해, 서버가 갑자기 중지되면 재시작할 때 저널을 사용함으 로써 종료 전에 디스크에 플러시되지 않은 쓰기를 다시 진행할 수 있다.

* 저널 파일의 크기제한은 100메가바이트 제한을 초과하면 새 저널을 만들어 씀
* 새 체크포인트가 작성되면 오래된 저널파일을 자동으로 제거

## 쓰기 결과 확인을 사용하는 클러스터 수준의 영속성

몽고디비는 모든 삽입과 갱신 메서드에 대해 쓰기 결과 확인을 지정하도록 지원한다

```
db.products.insertOne(
  {~~~},
  {writeConcern: {w : "majority", wtimeout:100}}
)
```

* 위 설정은 복제셋 모두에 성공적으로 복제됐을때 쓰기 성공.
* 100미리초 이내 복제되지 않으면 오류 반환

w 옵션말고 j옵션을 줘서 저널에 기록됐다는 확인을 요청할수도있다.

저널링되기를 기다리지 않으면, 각 멤버에 대해 약 100밀리초 동안은 서버 프로세스나 하드웨 어가 다운되면 쓰기가 손실된다. 하지만 복제 셋의 멤버에 대한 쓰기를 승인하기 전에 저널링 되기를 기다리면 성능이 저하된다.

## 읽기 결과확인을 사용하는 클러스터 수준의 영속성

쓰기가 영속되기 전에 쓰기 결과를 볼 수 있다.

기본적으로 readConcern은 local이며 복제셋멤버 대다수에 저장됐다는 보장 없이 데이터를 반환한다.

## 쓰기 결과 확인을 사용하는 트랜잭션의 영속성

여러 도큐먼트를 갱신할 때 원자성이 필요한 경우가 있으므로, 몽고DB는 복제 셋에 대 해 다중 도큐먼트 트랜잭션을 수행하는 기능을 제공한다.

트랜잭션을 사용하면 트랜잭션 내의 모든 데이터 변경이 성공해야만 한다. 작업이 실패하면 트 랜잭션이 중단되고 모든 데이터 변경 사항이 삭제된다. 모든 작업이 성공하면 트랜잭션의 모든 데이터 변경 사항이 저장되고 쓰기가 향후 읽기에 표시된다.

트랜잭션에 쓰기 결과확인 1을 사용하는경우 롤백 가능. majority를 사용해 네트워크와 서버 장애애도 지속되도록 할 수 있다.

### **쓰기 결과 확인이란?**

- MongoDB의 **Write Concern**은 쓰기 작업(예: `insert`, `update`, `delete`)이 얼마나 안전하게 수행되었는지를 보장하는 옵션입니다.
- 트랜잭션에서도 **쓰기 결과 확인** 설정이 중요합니다.

### **쓰기 결과 확인 옵션**

- `w: 1` (기본값)
  - 쓰기가 **주 노드(primary)**에 도달하면 작업이 성공한 것으로 간주합니다.
  - **장점**: 빠르지만 네트워크 장애나 주 노드의 문제가 발생하면 데이터 손실 가능성이 있습니다.
- `w: majority`
  - 쓰기가 **복제 셋의 과반수 노드**에 반영되었을 때 성공으로 간주합니다.
  - **장점**: 높은 내구성 보장. 네트워크나 서버 장애에도 데이터 보존 가능.
  - **단점**: 쓰기 작업 속도가 느려질 수 있음.

```java
@Configuration
public class MongoConfig {

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        MongoTransactionManager transactionManager = new MongoTransactionManager(dbFactory);
        
        // Write Concern 설정: "majority"
        dbFactory.getMongoDatabase().withWriteConcern(WriteConcern.MAJORITY);

        return transactionManager;
    }
}
```

