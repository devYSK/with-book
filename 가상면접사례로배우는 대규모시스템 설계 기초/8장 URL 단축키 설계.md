# 8장 URL 단축키 설계

[toc]





# 요구사항

- 쓰기 연산 : 매일 1억 개의 URL 생성
- 초당 쓰기 연산 1억(100million) / 24 / 3600 = 1160
- 읽기 연산 : 읽기 연산과 쓰기 연산 비율은 10:1 이라고 하자. 그 경우 읽기 연산은 초당 11,600회 발생한다. (1160 x 10 = 11,600)
- URL 단축 서비스를 10년간 운영한다고 가정하면 1억 x 365 x 10 = 3650억 개의 레코드를 보관해야 한다.
- 축약 전 URL의 평균 길이는 100이라고 하자.
- 따라서 10년 동안 필요한 저장 용량은 3650억 x 100바이트 = 36.5TB이다.

## 시스템의 기본적 기능

1. ﻿﻿﻿URL 단축: 주어진 진 URL을 훨씬 짧게 줄인다.
2. ﻿﻿﻿URL 리디렉션(redirection): 축약된 URL로 HTTP 요청이 오면 원래 URL로 안내
3. ﻿﻿﻿높은 가용성과 규모 확장성, 그리고 장애 감내가 요구됨



# 개략적 설계



## API 엔드포인트

클라이언트는 서버가 제공하는 API 엔드포인트를 통해 서버와 통신한다.

이 엔드포인트를 REST 스타일로 설계 해보자.

URL 단축기는 기본적으로 두 개의 엔드포인트를 필요로 한다.

1. URL 단축용 엔드포인트 : 새 단축 URL을 생성하고자 하는 클라이언트는 이 엔드포인트에 단축할 URL을 인자로 실어서 POST 요청을 보내야 한다. 

   **POST /api/v1/data/shorten**

   - 인자 : 

   - ```json
     // request body
     {
       longUrl: longURLstring 
     }
     ```

   - 반환: 단축 URL

     - ```json
       // response body 
       {
         shortUrl : adfsadf~
       }
       ```

     - 

2. URL 리다이렉션용 엔드포인트 : 단축 URL에 대해서 HTTP 요청이 오면 원래 URL로 보내주기 위한 용도의 엔드포인트로, 다음과 같은 형태를 띤다.

   **GET /api/v1/{shortUrl}**

   - 반환 : HTTP 리다이렉션 목적지가 될 원래 URL



## URL 리디렉션

다음의 그림은 브라우저에 단축 URL을 입력하면 무슨 일이 생기는지 보여준다.

<img src="./images/8장 URL 단축키 설계//image-20230827230546204.png">

단축 URL을 받은 서버는 그 URL을 원래 URL로 바꾸어서 301 응답의 Location 헤더에 넣어 반환한다.

<img src="./images/8장 URL 단축키 설계//image-20230827230627030.png" width = 700 height = 600>

## 301 응답 VS 302 응답

301 응답과 302 응답 둘다 리다이렉션 응답이지만, 차이가 있다.

### 301 Permanetly Moved

이 응답은 해당 URL에 대한 HTTP 요청의 처리 책임이 영구적으로 Location 헤더에 반환된 URL로 이전되었다는 응답이다.

영구적으로 이전되었으므로, 브라우저는 이 응답을 캐시(cache)한다.

`따라서 추후 같은 단축 URL에 요청을 보낼 필요가 있을 때 브라우저는 캐시된 원래 URL로 요청을 보내게 된다.`

### 302 Found

이 응답은 주어진 URL로의 요청이 **일시적으로 Location 헤더가 지정하는 URL에 의해 처리되어야 한다는 응답이다.**

`따라서 클라이언트의 요청은 언제나 단축 URL 서버에 먼저 보내진 후에 원래 URL로 리다이렉션 되어야 한다.`



서버 부하를 줄이는 것이 중요하다면 301 Permanent Moved를 사용하는 것이 좋다

*  첫 번째 요청만 단축 URL 서버로 전송될 것이기 때문이다.

하지만 트래픽 분석이 중요할 때는 302 Found를 쓰는 쪽이 클릭 발생률이나 발생 위치를 추적하는 데 좀 더 유리할 것이다.



URL 리다이렉션을 구현하는 가장 직관적인 방법은 해시 테이블을 사용하는 것이다.

해시 테이블에 <단축 URL, 원래 URL>의 쌍을 저장한다고 가정한다면, URL 리다이렉션은 다음과 같이 구현될 수 있을 것이다.

- 원래 URL = 

```java
hashTable.get(단축 URL)
```

- 301 또는 302 응답 Location 헤더에 원래 URL을 넣은 후 전송

```java
@RestController
public class UrlController {
    
    private final UrlStore urlStore; // hastTable을 구현했다고 가정

    @GetMapping("/api/v1{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {
        String originalUrl = urlStore.getOriginalUrl(shortUrl);
        
        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }
      
        return ResponseEntity.status(302)  // Or use 301 for permanent redirect
                .location(URI.create(originalUrl))
                .build();
    }

    @PostMapping("/api/v1/data/shorten")
    public String storeUrl(@RequestBody String longUrl) {
        String shortUrl = urlStore.store(originalUrl);
    
        return shortUrl;
    }
  
}
```

## URL 단축 FLOW

단축 URL이 `www.tinyurl.com/{hashValue}` 같은 형태라고 가정

결국 중요한 것은 긴 URL을 이 해시 값으로 대응시킬 해시 함수 fx를 찾는 일이 될 것이다.

<img src="./images/8장 URL 단축키 설계//image-20230827231339393.png" width = 500 height = 350>

이 해시 함수는 다음 요구 사항을 만족해야 한다.

- 입력으로 주어진 긴 URL이 다른 값이면 해시 값도 달라야 한다.
- 계산된 해시 값은 원래 입력으로 주어졌던 긴 URL로 복원될 수 있어야 한다.



# 상세 설계

데이터 모델, 해시 함수, URL. 단축 및 리디렉션에 관 한 보다 구체적인 설계안을 만들어 보겠다.

## 데이터 모델

위에서 언급했듯이 모든 것을 해시 테이블에 두었었다. 이 접근법은 초기 전략으로는 괜찮지만 실제 시스템에 쓰기에는 곤란한데, 메모리는 유한한 데다 비싸기 때문이다.

더 나은 방법은 <단축 URL, 원래 URL>의 순서쌍을 관계형 데이터베이스에 저장한는 것이다.

이 테이블은 단순화된 것으로 id, shortUrl, longURL의 세 개 컬럼을 갖는다.

```
+-----+---------+----------------------------------+
| id  | shortUrl|            longUrl               |
+-----+---------+----------------------------------+
|  1  | abc123  | https://www.example.com/page1    |
|  2  | def456  | https://www.example.com/page2    |
| ... | ...     | ...                              |
+-----+---------+----------------------------------+
```

- `id`: 행의 고유 식별자. 관계형 데이터베이스에서 자주 사용되는 기본 키로
- `shortUrl`: 단축 URL의 끝 부분 (예: "abc123").
- `longUrl`: 원래의 긴 URL (예: "https://www.example.com/page1").

## 해시 함수

해시 함수는 원래 URL을 단축 URL로 변환하는 데 쓰인다. 

편의상 해시 함수가 계산하는 단축 URL 값을 hashValue라고 지칭.



### 해시 값 길이

hashValue는 [0-9, a-z, A-Z]의 문자들로 구성된다. 

* 따라서 사용할 수 있는 문자의 개수는 10 + 26 + 26 = 62개다.

hashValue의 길이를 정하기 위해서는 3650억인 n의 최솟값을 찾아야 한다.

이전 요구사항에서 계산했던 추정치에 따르면 `이 시스템은 3650억 개의 URL을 만들어 낼 수 있어야 한다.`

다음의 표는 **hashValue의 길이(n)**와, 해시 함수가 만들 수 있는 URL의 개수(조합) 사이의 관계를 나타낸다.

| n    | URL 개수                   |
| ---- | -------------------------- |
| 1    | 62^1 = 62                  |
| 2    | 62^2 = 3,844               |
| 3    | 62^3 = 238,328             |
| 4    | 62^4 = 14,776,336          |
| 5    | 62^5 = 916,132,832         |
| 6    | 62^6 = 56,800,235,584      |
| 7    | 62^7 = 3,521,614,606,208   |
| 8    | 62^8 = 218,340,105,584,896 |

n = 7 이면 3.5조 개의 URL을 만들 수 있다. 그러면 3650억개를 만들 수 있는 요구사항을 만족시키기 충분한 값이다.

따라서 hashValue의 길이는 7로 하면 된다.

해시 함수 구현에 쓰일 기술로는 두 가지 방법을 살펴보자.

하나는 해시 후 충돌 해소 방법이고, 다른 하나는 base-62 변환 법이다.

### 해시 함수 구현 - 해시 후 충돌 해소 방법

긴 URL을 줄이려면, 원래 URL을 7글자 문자열로 줄이는 해시 함수가 필요하다.

손쉬운 방법은 CRC32, MD5, SHA-1같이 잘 알려진 해시 함수를 이용하는 것이다.

아래는 해시 함수를 사용하여 https://en.wikipedia.org/wiki/Systems_design 을 축약한 결과다.

| 해시 함수 | 해시 결과 (16진수)                       | 길이 (자연수) | 설명                                                         |
| --------- | ---------------------------------------- | ------------- | ------------------------------------------------------------ |
| CRC32     | 5cb54054                                 | 8             | 순환 중복 검사 (CRC)의 32비트 버전. <br />빠르게 계산되며, 주로 데이터의 무결성 검사에 사용됩니다. |
| MD5       | 5a62509a84df9ee03fe1230b9dfb84e          | 32            | 128비트 암호화 해시 함수. <br />많이 사용되었지만 현재는 보안 취약점이 발견되어 권장되지 않습니다. |
| SHA-1     | 0eeae7916c06853901d9ccbefbfcaf4de57ed85b | 40            | 보안 해시 알고리즘 (SHA)의 첫 번째 버전. <br />160비트 출력을 가지며, MD5와 마찬가지로 현재는 보안 취약점으로 인해 권장되지 않습니다. |

그런데 위의 표와 같이, `CRC32가 계산한 가장 짧은 해시값조차도 7보다는 길다.`

어떻게 하면 줄일 수 있을까?

이 문제를 해결할 첫 번째 방법은 계산된 해시 값에서 처음 7개 글자만 이용한는 것인데, ** 이렇게 하면 해시 결과가 서로 충돌할 확률이 높아진다. **

충돌이 실제로 발생했을 때는, 충돌이 해소될 때까지 사전에 정한 문자열을 해시값에 덧붙인다.

<img src="./images/8장 URL 단축키 설계//image-20230827234423875.png" width = 800 height = 500>

이 방법을 쓰면 충돌은 해소할 수 있지만 단축 URL을 생성할 때 한 번 이상 데이터베이스 질의를 해야하므로 오버헤드가 크다.

데이터베이스 대신 블룸 필터를 사용하면 성능을 높일 수 있다.

####  **블룸 필터**

블룸 필터(Bloom Filter)는 데이터 구조 중 하나로, 집합 내에 원소가 존재하는지 여부를 효율적으로 확인할 수 있는 확률적 데이터 구조

<img src="./images/8장 URL 단축키 설계//image-20230827234646870.png">

* https://ko.wikipedia.org/wiki/%EB%B8%94%EB%A3%B8_%ED%95%84%ED%84%B0

블룸 필터에 의해 어떤 원소가 집합에 속한다고 판단된 경우 실제로는 원소가 집합에 속하지 않는 긍정 오류가 발생하는 것이 가능하지만(거짓 긍정),

반대로 원소가 집합에 속하지 않는 것으로 판단되었는데 실제로는 원소가 집합에 속하는 부정 오류는 절대로 발생하지 않는다는 특성이 있다.(거짓 부정 없음)

집합에 원소를 추가하는 것은 가능하나, 집합에서 원소를 삭제하는 것은 불가능하다.

블룸 필터의 활용 사례:

- 데이터베이스 시스템에서 디스크에 있는 데이터를 조회하기 전에 빠르게 데이터의 존재 여부를 확인할 수 있다.
- 캐싱 시스템에서 블룸 필터를 사용하여 데이터가 캐시에 없는지 빠르게 확인할 수 있다.
- 인터넷 광고 시스템에서 사용자가 본 광고를 추적하고, 중복 광고를 방지하는 데 사용될 수 있다.

자바 코드

* https://gngsn.tistory.com/201

```java
import java.util.BitSet;

public class BloomFilter {
    private static final int DEFAULT_SIZE = 2 << 24;
    private static final int[] seeds = new int[]{7, 11, 13, 31, 37, 61};
    private BitSet bits = new BitSet(DEFAULT_SIZE);
    private SimpleHash[] func = new SimpleHash[seeds.length];

    public BloomFilter() {
        for (int i = 0; i < seeds.length; i++) {
            func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
        }
    }

    public void add(String value) {
        for (SimpleHash f : func) {
            bits.set(f.hash(value), true);
        }
    }

    public boolean contains(String value) {
        if (value == null) {
            return false;
        }

        boolean ret = true;
        for (SimpleHash f : func) {
            ret = ret && bits.get(f.hash(value));
        }
        return ret;
    }

	 public static class SimpleHash {
        private int cap;
        private int seed;

        public SimpleHash(int cap, int seed) {
            this.cap = cap;
            this.seed = seed;
        }

        public int hash(String value) {
            int result = 0;
            int len = value.length();
            for (int i = 0; i < len; i++) {
                result = seed * result + value.charAt(i);
            }
            return (cap - 1) & result;
        }
    }

    public static void main(String[] args) {
        BloomFilter filter = new BloomFilter();
        filter.add("testString");
        System.out.println(filter.contains("testString")); // true
        System.out.println(filter.contains("anotherString")); // false
    }
}
```



### 해시 함수 구현 -  base-62 변환

진법 변환은 URL 단축기를 구현할 때 흔히 사용되는 접근법 중 하나다.

이 기법은 수의 표현 방식이 다른 두 시스템이 같은 수를 공유하여야 하는 경우에 유용하다.

`62진법을 쓰는 이유는 hashValue에 사용할 수 있는 문자 개수가 62개이기 때문이다.`

*  Base64에서 62번째(+), 63번째(/) 기호는 url에서 parameter로 사용할 수 없기 때문에, 제외한 것.

11157₁₀을 62진수로 변환해보자.

* 11157은 DB의 ID일 확률이 높다.
  * ID 값은 어떤 긴 URL에 대한 고유 식별자로 사용되고, 이를 base-62로 변환하여 짧은 URL을 생성하는 과정

- 62진법은 수를 표현하기 위해 62개의 문자를 사용하는 진법이다. 
- 따라서 0은 0으로 9는 9로, 10은 a로 61은 Z로 대응시켜 표현하도록 할 것이다. 
- 따라서 62진법에서 ‘a’는 10을 나타내고 ‘Z’는 61을 나타낸다.
- 11157₁₀ = 2 x 622 + 55 X 621 + 59 x 620 = [2, 55, 59] => [2, T, X] => 2TX62 이다.
- 따라서 단축 URL은 https://tinyurl.com/2TX가 된다.

<img src="./images/8장 URL 단축키 설계//image-20230827235248504.png">

변환 절차는 다음과 같습니다:

1. 주어진 숫자를 62로 나눕니다.
2. 몫을 다시 62로 나누는 과정을 계속 반복합니다.
3. 나머지를 기반으로 62진법의 값을 얻습니다.

따라서 11157을 62진법으로 변환할 때:

1. 11157을 62로 나누면 몫은 179, 나머지는 59입니다. 59는 62진법에서 'X'에 해당합니다.
2. 179를 62로 나누면 몫은 2, 나머지는 55입니다. 55는 62진법에서 'T'에 해당합니다.
3. 2는 나눌 수 없으므로 그대로 2입니다.

```java
public class Base62Converter {
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public String encode(long value) {
        if (value == 0) {
            return String.valueOf(BASE62[0]);
        }

        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE62[(int) (value % 62)]);
            value /= 62;
        }
        
        return sb.reverse().toString();
    }

    public long decode(String base62String) {
        long result = 0;
        for (char character : base62String.toCharArray()) {
            result = result * 62 + new String(BASE62).indexOf(character);
        }
        return result;
    }

    public static void main(String[] args) {
      
        Base62Converter converter = new Base62Converter();
      
        long number = 11157;
        String base62Value = converter.encode(number);
        System.out.println("Encoded: " + base62Value);
        System.out.println("Decoded: " + converter.decode(base62Value));
    }
  
}
```





URL 숏트너 고수 글

* https://monday9pm.com/%EC%B4%88%EB%B3%B4-%EA%B0%9C%EB%B0%9C%EC%9E%90-url-shortener-%EC%84%9C%EB%B2%84-%EB%A7%8C%EB%93%A4%EA%B8%B0-1%ED%8E%B8-base62%EC%99%80-%EC%B6%A4%EC%9D%84-9acc226fb7eb



### 두 접근법 비교

| 해시 후 충돌 해소 전략                                       | base-62 변환                                                 |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| 단축 URL의 길이가 고정됨                                     | 단축 URL의 길이가 가변적. ID 값이 커지면 같이 길어짐         |
| 유일성이 보장되는 ID 생성기가 필요치 않음                    | 유일성 보장 ID 생성기가 필요                                 |
| 충돌이 가능해서 해소 전략이 필요                             | ID 유일성이 보장된 보장된 후에야 적용 가능한 전략이라 충돌은 아예 불가능 |
| ID로부터 단축 URL을 계산하는 방식이 아니라서 다음에 쓸 수 있는 URL을 알아내는 것이 불가능 | ID가 1씩 증가하는 값이라고 가정하면 다음에 쓸 수 있는 단축 URL이 무엇인지 쉽게 알아낼 수 있어서 보안상 문제가 될 소지가 있음 |



# URL 단축기 상세 설계

URL 단축기는 시스템의 핵심 컴포넌트이므로, 그 처리 흐름이 논리적으로는 단순해야 하고 기능적으로는 언제나 동작하는 상태로 유지되어야 한다. 

* 본 예제에서는 62진법 변환 기법을 사용해 설계할 것이다

<img src="./images/8장 URL 단축키 설계//image-20230828000620963.png" width = 700 height = 600>

1. ﻿﻿﻿입력으로 긴 URL을 받는다.
2. ﻿﻿﻿데이터베이스에 해당 URL이 있는지 검사한다.
3. ﻿﻿﻿데이터베이스에 있다면 해당 URL에 대한 단축 URL을 만든 적이 있는 것이다. 따라서 데이터베이스에서 해당 단축 URL을 가져와서 클라이언트에게 반환한다.
4. ﻿﻿﻿데이터베이스에 없는 경우에는 해당 URL은 새로 접수된 것이므로 유일한
    ID를 생성한다. 이 ID는 데이터베이스의 기본 키로 사용된다.
5. ﻿﻿﻿62진법 변환을 적용, ID를 단축 URL로 만든다.
6. ﻿﻿﻿ID, 단축 URL, 원래 URL로 새 데이터베이스 레코드를 만든 후 단축 URL을 클라이언트에 전달한다.

이해가 조금 어렵다면 아래의 에제를 보도록 하자.

- ﻿﻿입력된 URL이 https://en.wikipedia.org/wiki/Systems_design이라고 하자.
- ﻿﻿이 URL에 대해 ID 생성기가 반환한 ID는 2009215674938이다.
- ﻿﻿이 ID를 62진수로 변환하면 2ngedcu를 얻는다.
- ﻿﻿아래 표 84와 같은 새로운 데이터베이스 레코드를 만든다.

| ID            | shortURL | longURL                                      |
| ------------- | -------- | -------------------------------------------- |
| 2009215674938 | zngedcu  | https://en.wikipedia.org/wiki/Systems_design |

이 생성기의 주된 용도는, `단축 URL을 만들 때 사용할 ID를 만드는 것`이고, `이 ID는 전역적 유일성(globally unique)이 보장되는 것`이어야 한다. 

고도로 분산된 환경에서 생성기를 만드는 것은 무척 어렵지만 7장에서 분산 ID 생성기를 구현하는 몇 가지 방법을 살펴본 적이 있다.

* 스노우 플레이크, ulid



# URL 리디렉션 상세 설계

URL 리디렉션(recirection) 메커니즘의 상세한 설계를 그리고 있다.

쓰기보다 읽기를 더 자주 하는 시스템이라, `<단축 URL, 원래 URL>`의 쌍을 캐시에 저장하여 성능을 높였다.

<img src="./images/8장 URL 단축키 설계//image-20230828000928213.png">

로드밸런서의 동작 흐름은 다음과 같이 요약할 수 있다.

1. ﻿﻿﻿사용자가 단축 URL을 클릭한다.
2. ﻿﻿﻿로드밸런서가 해당 클릭으로 발생한 요청을 웹 서버에 전달한다.
3. ﻿﻿﻿단축 URL이 이미 캐시에 있는 경우에는 원래 URL을 바로 꺼내서 클라이언 트에게 전달한다.
4. ﻿﻿﻿캐시에 해당 단축 URL이 없는 경우에는 데이터베이스에서 꺼낸다. 데이터 베이스에 없다면 아마 사용자가 잘못된 단축 URL을 입력한 경우일 것이다.
5. ﻿﻿﻿데이터베이스에서 꺼낸 URL을 캐시에 넣은 후 사용자에게 반환한다.



# 마무리 - 설계 후

- ﻿﻿처리율 제한 장치(rate limiter): 지금까지 살펴본 시스템은 엄청난 양의 URL 단축 요청이 밀려들 경우 무력화될 수 있다는 잠재적 보안 결함을 갖고 있다. 처리율 제한 장치를 두면, IP 주소를 비롯한 필터링 규칙(filtering rule) 들을 이용해 요청을 걸러낼 수 있을 것이다. 
- ﻿﻿웹 서버의 규모 확장: 본 설계에 포함된 웹 계층은 무상태(stateless) 계층이 므로, 웹 서버를 자유로이 중설하거나 삭제할 수 있다.
- ﻿﻿데이터베이스의 규모 확장: 데이터베이스를 다중화하거나 샤딩(sharding)하여 규모 확장성을 달성할 수 있다.
- ﻿﻿데이터 분석 솔루션(analytics): 성공적인 비즈니스를 위해서는 데이터가 중요하다. URI 단축기에 데이터 분석 솔루션을 통합해 두면 어떤 링크를 얼마나 많은 사용자가 클릭했는지, 언제 주로 클릭했는지 등 중요한 정보를 알아 낼 수 있을 것이다.



