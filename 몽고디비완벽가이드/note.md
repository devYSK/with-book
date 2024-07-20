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

## $project