# 3장 트랜잭션 제어 & 사용자 관리



[toc]



# 3.1 Isolation & lock

NoSQL로 분류되는 모든 제품이 트랜잭션을 제어할 수 있고, 일관성과 공유 기능을 제공하는 것은 아니지만 Redis는 트랜잭션 제어가 가능하다

**Redis는 Read Commited 타입의 트랜잭션 제어 타입도 제공한다**

관계형 DB처럼 commit, rollback을 수행하게 되면 초당 100,000~200,000건 이상 빅데이터의 빠른 쓰기와 읽기 작업에 좋은 성능을 기대할 수 없게 되는 문제가 발생하게 되는데, 이를 보완가히 위해 Redis는 Read Commited 타입의 트랜잭션 제어 타입도 제공함

\- 읽기 일관성과 데이터 공유를 위해 Data Sets(Key/Value) Lock을 제공

\- 트랜잭션 제어를 위해 Read Uncommitted와 Read Committed 타입의 2가지 유형을 제공



# 3.2 CAS (Check and Set)

데이터 일관성 공유(동일한 데이터를 동시 여러명의 사용자가 수정/삭제하는 경우 발생하는 충돌을 피하기 위한 기술)를 위해서 동시 처리가 발생할 때 먼저 작업을 요구한 사용자에게 우선권을 보장하고, 나중에 작업을 요구한 사용자의 세션에서는 해당 트랜잭션에 충돌이 발생했음을 인지할 수 있도록 하는것을 CAS 라고 한다.

Redis는 이와 같은 경우 `Watch 명령어`에 의해 트랜잭션을 취소할 수 있다.

**실습**

```
> WATCH                # 다중 트랜잭션이 발생하는지 여부를 모니터링을 시작함
> MULTI                # 트랜잭션의 시작
OK
> Set 1 ysk         # 임시 저장
QUEUED                 # 임시 저장
Set 2 ysk2
QUEUED                 # 임시 저장

> EXEC                 # 트랜잭션 종료(commit)
```

# 3.3 Commit & rollback

**1. EXEC**

변경한 데이터를 최종 저장 시 사용

**2. DISCARD**

변경한 데이터를 최종 저장하지 않고 취소할 때 사용

```
> MULTI                # 트랜잭션의 시작
OK
> Set 1 ysk         # 임시 저장
QUEUED
> Set 2 ysk2            # 임시 저장
QUEUED
>
> EXEC                 # 트랜잭션 종료(commit)
1) OK
2) OK
>
> keys &
1) "2"
2) "1"
>
> FLUSHALL
OK

# 다른 트랜잭션 시작 
> MULTI
OK
> Set 3 ysk
QUEUED
> Set 4 ysk4
QUEUED

> DISCARD              # 트랜잭션 종료(rollback) 롤백되므로 저장 X
OK

> keys *
(empty list or set)
```

# 3.4 Index 유형 및 생성

**1. Index 유형**

Redis DB는 기본적으로 하나의 Key와 하나 이상의 Field/Element 값으로 구성되는데, 해당 Key에는 빠른 검색을 위해 기본적으로 인덱스가 생성된다. 

* Primary Key Index : 기본적으로 생성되는 인덱스
*  Secondary Key Index : 사용자의 필요에 따라 추가적인 인덱스를 생성한 것

**실습**

### Sorted Sets 타입 입덱스

```sh
# order 테이블의 ship_date 필드에 대한 인덱스를 생성합니다.
# 이때, 각 범위에 대한 점수(score)가 지정됩니다
> zadd order.ship_date.index 2 '20220319:20220331'
(integer) 1
> zadd order.ship_date.index 1 '20220320:20220330'
(integer) 1                     # order 테이블, order_no:ship_date 필드에 인덱스 생성

# 생성된 인덱스 범위를 점수 순서대로 출력합니다.
> zrange order.ship_date.index 0 -1
1) "20220320:20220330"
2) "20220319:20220331"

# 인덱스의 모든 요소를 스캔합니다.
> zscan order.ship_date.index 0
1) "0"
2) 1) "20220320:20220330"
   2) "1"
   3) "20220319:20220331"
   4) "2"

# 인덱스에서 특정 패턴과 일치하는 요소를 스캔합니다.
> zscan order.ship_date.index 0 match 20220319*
1) "0"
2) 1) "20220319:20220331"
   2) "2"

# order 테이블의 ship_date:order_no 필드에 대한 인덱스를 생성합니다.
> zadd order.no.index 1 20220320    # order, ship_date:order_no 필드에 인덱스 생성
(integer) 1
> zadd order.no.index 2 20220319
(integer) 1

# 인덱스의 모든 요소를 스캔합니다.
> zrange order.no.index 0 -1
1) "20220320"
2) "20220319"
```

# 3.5 사용자 생성 및 인증 / 보안 / roles

**1. 액세스 컨트롤 권한(Access Control Privilege)**

**1. 미리 DB 내에 사용자 계정과 비밀번호를 생성해두고, Redis Server에 접속하려는 사용자는 해당 계정과 비밀번호를 입력하여 허가 받는 방법**

잘못된 계정과 비밀번호를 입력하게 되면 접속 에러가 발생함

```sh
vi redis.conf
requirepass manager

or

vi redis.conf
masterauth redis123
```

2. OS 인증 & Internal 인증

reids는 IP만으로 접속을 허용하는 network 인증방식을 제공한다

```sh
vi redis.conf

bind_ip 192.168.0.10
```

3. internal 인증

Redis Server에 접속한 다음 auth 명령어로 미리 생성해둔 사용자 계정과 비밀번호를 입력하여 권한을 부여받는 방법

```
> auth 1234
```

**액세스 컨트롤 기능 활성화**

redis.conf 파일 내에 requirepass, masterauth 파라미터를 통해 환경설정해야 한다

### Enterprise Edition Server에서 제공되는 Role 유형과 작업 범위

| **유형**       | **설명**                                                     |
| -------------- | ------------------------------------------------------------ |
| Admin          | 시스템에 전체 액세스할 수 있음                               |
| DB Viewer      | - cluster의 모든 데이터베이스에 대한 정보를 볼 수 있음- node 및 cluster에 대한 정보를 볼 수 없음- 로그를 볼 수 있음- 자체 비밀번호 변경 이외에는 cluster 설정을 볼 수 없음 |
| Cluster Viewer | - cluster, node, 데이터베이스에 대한 모든 정보를 볼 수 있음- 로그를 볼 수 있음- 자체 비밀번호 변경 이외에는 cluster 설정을 볼 수 없음 |
| DB Member      | - 데이터베이스 생성이 가능함- DB Metric을 볼 수 있음- 데이터베이스 구성을 편집할 수 있음- slowlog를 클리어할 수 있음- 로그를 볼 수 있음- node 및 cluster에 대한 정보를 볼 수 있음- 자체 비밀번호 변경 이외에는 cluster 설정을 볼 수 없음 |
| Cluster Member | - node와 cluster 정보를 볼 수 있음- 데이터베이스 생성이 가능함- DB Metric을 볼 수 있음- 데이터베이스 구성을 편집할 수 있음- slowlog를 클리어할 수 있음- 로그를 볼 수 있음- 자체 비밀번호 변경 이외에는 cluster 설정을 볼 수 없음 |



# Redis ACL

ACL : Access Control List. 접근 제어

사용자를 생성하고 암호 지정, 사용 가능한 커맨드와 키를 지정 가능하다

- ACL 사용을 통해 보안성과 안전성 구현 가능하다
  - KEYS/FLUSHALL 호출 방지, 사용자에게 필요한 권한만 부여 등

* https://redis.io/docs/management/security/acl/

* http://redisgate.kr/redis/server/acl.php < 여기가 좀더 정확함

## 로그인 - AUTH 명령어

Redis [`AUTH`](https://redis.io/commands/auth)명령은 Redis 6에서 확장되었으므로 이제 두 개의 인수 형식으로 사용할 수 있다.

```
AUTH <username> <password>
```

## 사용자 생성하기 - ACL SETUSER

```null
ACL SETUSER user on|off > password keys commands
```

- `user` : user id 설정
- `on | off` : : 활성화 여부 설정. 사용자를 등록하지만 일단 사용하지 못하게 할 때 off 사용
  - 새로 로그인(auth) 할 수는 없지만, 이미 로그인한 사용자의 사용을 막을 수는 없음
- `> password` : 비밀번호 설정. 비밀번호는 `>` 구분자 다음에 지정
  - `nopass` : 비밀번호 없이 사용하려는 경우 지정. 로그인 시 아무 문자열이나 입력해도 됨
  - 비밀번호 저장 시 내부적으로 SHA-256 암호화 알고리즘 사용
- `keys` : 사용가능한 키 지정
  - 모든 키 허용 : `allkeys` 또는 `~*`
  - 특정 패턴 지정 : `~user*` 로 지정 시 user로 시작하는 키만 사용 가능
  - `~*` 상태에서 특정 패턴을 지정하려면 `resetkeys` 사용 ex) `ACL SETUSER user resetkeys ~user*`
- `commands` : 사용 가능한 커맨드 지정
  - 모든 커맨드 허용 : `allcommands` 또는 `+@all`
  - 모든 커맨드 비허용 : `nocommands` 또는 `-@all`
  - 커맨드 지정 : `+command`로 추가, `-command`로 빼기
  - 커맨드 그룹 지정 : `+@group`으로 추가, `-@group`으로 빼기
- ACL SETUSER 커맨드는 비밀번호가 포함되므로 로그에 남지 않는다

**관리자 user 설정**

```null
> ACL SETUSER admin on >password allkeys allcommands
```

조회 user 설정

```null
> ACL SETUSER reader on >password allkeys +@read
```

**Read/Write 가능한 일반 사용자 설정**

```null
> ACL SETUSER writer on >password allkeys allcommands -@dangerous
```

### 커맨드 그룹

### 커맨드 그룹 목록 조회

```null
> ACL CAT
 1) "keyspace"
 2) "read"
 3) "write"
 4) "set"
 5) "sortedset"
 6) "list"
 7) "hash"
 8) "string"
 9) "bitmap"
10) "hyperloglog"
11) "geo"
12) "stream"
13) "pubsub"
14) "admin"
15) "fast"
16) "slow"
17) "blocking"
18) "dangerous"
19) "connection"
20) "transaction"
21) "scripting"
```

- **keyspace**: del, expire, flushdb, keys, ttl, scan 등

- **read**: get, lrange, smembers, zrange, hget, xrange 등

- **write**: set, lpush, sadd, zadd, hset, xadd 등

- **set**: sadd, scard, srem, spop 등

- **sortedset**: zadd, zcard, srem, zpopmin 등

- **list**: lpush, llen, lrem, lpop 등

- **hash**: hset, hlen, ldel, hget 등

- **string**: set, get, incr 등

- **bitmap**: setbit, bitop, getbit 등

- **hyperloglog**: pfadd, pfmerge, pfcount, pfselftest

- **geo**: geoadd, geodist, georadius 등

- **stream**: xadd, xlen, xrange, xdel 등

- **pubsub**: publish, subscribe, pubsub 등

- admin

  : bgsave, config, debug, shutdown 등

  - admin 명령은 dangerous 그룹에 포함됨

- **fast**: get, lpush, hget 등

- **slow**: lrem, mset, save 등

- **blocking**: blpop, brpop, brpoplpush, bzpopmin, bzpopmax, xread, xreadgroup

- **dangerous** : flushall, keys, shutdown, failover, cluster, client, module 등

- **connection**: hello, client, auth, echo, ping, command

- **transaction**: watch, multi, unwatch, discard, exec

- **scripting**: evalsha, eval, script

### Dangerous 그룹

- 보안 위험, 성능 저하 위험이 있는 커맨드들이 포함됨
- flushall : 레디스 서버의 모든 데이터(key, value) 삭제
- flushdb : 현재 사용중인 DB의 모든 데이터(key, value) 삭제
- swapdb : 두 DB를 swap
- keys : 패턴에 일치하는 모든 key 반환 ➡️ scan 사용으로 대체
- shutdown : 레디스 종료
- monitor : 서버에서 실행되는 모든 커맨드 관찰/감시. monitor 수행 시 서버 성능 50% 하락
- role : 자신의 역할(master/slave/sentinel)과 부가 정보 조회
- sync : 내부 커맨드 replication
- psync : 내부 커맨드 partial replication
- replconf : 내부 명령 cluster
- pfselftest : 내부 명령 HyperLogLog
- sort : list 데이터를 정렬
- sort_ro : sort와 동일하지만 결과를 저장하지 않음
- migrate : data를 다른 redis server로 옮기기. 내부적으로 dump, restore, del 명령이 실행됨
- restore, restore-asking : serialized value를 deserialize하여 저장
- failover : master와 replica 변경
- info : 레디스 서버 정보와 통계값 조회
- debug, pfdebug : redis 개발/테스트 용도. 여러 관리 명령 실행 가능
- save : RDB 파일 저장 (foreground 수행). 수행 완료까지 서버가 다른 일 수행 불가
- bgsave : RDB 파일 저장 (background 수행). 하위 프로세스를 생성하여 수행
- bgrewriteaof: AOF 파일 저장(background 수행). 하위 프로세스를 생성하여 수행
- lastsave : RDB 마지막 저장 일시를 timestamp 형식으로 출력
- slaveof : 슬레이브 설정 변경
- replicaof : 복제노드 설정 변경
- config : 서버 설정 변경
- client : 클라이언트 관리 (조회, 제거, 이름 변경 등)
- cluster : 클러스터 설정 변경
- module : 모듈 관리
- slowlog : 명령 성능 측정/기록. 성능 저하 발생
- latency : 서버 성능 분석. 성능 저하 발생
- acl : 사용자 관리

### 그룹의 커맨드 목록 조회

```null
> ACL CAT group
```

- 커맨드가 어느 그룹에 속하는지 확인

  ```null
  > COMMAND INFO 커맨드명
  ```

## 사용자 삭제 - DELUSER

```
ACL DELUSER user명
```

* Default user 삭제 불가

## ACL file

사용자 정보 저장

```null
> ACL SAVE
```

- 사용자 정보를 redis.conf에 설정된 옵션에 따라 acl file에 저장
- 저장하는 사용자 정보는 `ACL LIST`의 결과와 같음

사용자 정보 로드

```null
> ACL LOAD
```

- redis 서버 시작 시 acl file에서 사용자 정보 로드

### ACL(Auth) 로그

```null
> ACL LOG [<count> | RESET]
```

- 실패한 로그인 정보 조회

## ACL과 복제(replica), 센티널 (sentinel), 클러스터(cluster)

* http://redisgate.kr/redis/server/acl.php

### 복제(replica)와 ACL

- 마스터와 복제 노드의 redis.conf에 masteruser와 masterauth를 설정한다. Masteruser와 masterauth는 aclfile에 지정한 권한 있는 사용자를 사용한다.
  예) masteruser admin, masterauth password
- 마스터와 복제 노드에 동일한 aclfile을 사용한다.

### 센티널(sentinel)과 ACL

- sentinel.conf 파일에 sentinel auth-user <master-name> <username>를 지정한다. 이것은 sentinel auth-pass 파라미터와 짝을 이루어 레디스 서버를 모니터하고 장애조치(failover)하는데 사용된다.
  레디스 서버에 같은 user와 password가 적용되어 있어야 한다.

### 레디스 클러스터(cluster)와 ACL

- 클러스터는 아직 ACL을 지원하지 않는다. 클러스터를 구성할 수는 있으나 마스터 다운 시 failover가 진행되지 않는다.