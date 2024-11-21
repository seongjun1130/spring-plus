# SPRING PLUS
## 3-3
>![](https://velog.velcdn.com/images/lionjojo/post/421e1969-9146-445e-89b8-270fa0136dce/image.png)


### RDS 설정
![](https://velog.velcdn.com/images/lionjojo/post/509bcf9d-44ee-4ede-888b-adb3780f869d/image.png)![](https://velog.velcdn.com/images/lionjojo/post/6ea8e5e2-2448-456a-9387-c79c464eae37/image.png)![](https://velog.velcdn.com/images/lionjojo/post/5858bd5f-1012-4c88-9bc7-f3c471b44846/image.png)
- RDS 와의 연결설정을 확인 가능하다.
- RDS 가 퍼블릭액세스가 거부되있을경우 접근이 불가능할 수 도 있으니 꼭 확인해보자!.
![](https://velog.velcdn.com/images/lionjojo/post/6913f2d2-3ec1-4929-a217-3cdd2f372ce6/image.png)

---

## 3-4
>![](https://velog.velcdn.com/images/lionjojo/post/2321b621-0eb3-4ba6-84fe-1d9dbed26b51/image.png)

### 유저 데이터 추가 코드
```java
@Repository
@RequiredArgsConstructor
public class UserBulkRepository {
  private final JdbcTemplate jdbcTemplate;

  @Transactional
  public void saveAll(List<User> Users) {
    jdbcTemplate.batchUpdate(
        "insert into users(email,password,nickname,user_role,created_at,modified_at) values (?, ?, ?, ?,?,?)",
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            User user = Users.get(i);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getNickname());
            ps.setString(4, user.getUserRole().toString());
            ps.setString(5, LocalDateTime.now().toString());
            ps.setString(6, LocalDateTime.now().toString());
          }

          @Override
          public int getBatchSize() {
            return Users.size();
          }
        });
  }
}
```
```java
@SpringBootTest
class AuthServiceTest {
  private static final Random RANDOM = new Random();

  @Autowired private UserBulkRepository userBulkRepository;

  private static final char[] KOREAN_CHARACTERS =
      ("가나다라마바사아자차카타파하김이박최조장윤임강한오서권황안송류홍배진차원유심구노물철산별빛길불동고성준혁명호의완연시누리재현익수신희상원진윤주민기백욱금여승육헌은영도식창용환시우지수정도협훈인배옥로문손초일탁태제월린삼섬실")
          .toCharArray();

  @Test
  @Rollback(false)
  @Transactional
  @DisplayName("더미유저데이터 생성")
  void generateMillionUsers() {
    long startTime = System.currentTimeMillis();
    Set<String> nicknameSet = Collections.synchronizedSet(new HashSet<>());
    List<User> users = new ArrayList<>();
    for (long i = 0; i < 1000000; i++) {
      String email = "user" + i + "@example.com";
      String nickname = generateUniqueNickname(nicknameSet);
      String password = "password" + i;
      UserRole userRole = UserRole.USER;
      User user = new User(email, password, nickname, userRole);
      users.add(user);
      if (users.size() == 100000) {
        userBulkRepository.saveAll(users);
        users.clear();
      }
    }
    long endTime = System.currentTimeMillis();
    System.out.println(endTime - startTime);
  }
}
```
- batchSize 를 통해 BulkInsert 방식을 사용하여 유저데이터 100만개를 삽입하였다.
- 실행속도 39376 ms (39초)

### 개선 전
![](https://velog.velcdn.com/images/lionjojo/post/dfd9fe88-0c7a-4f03-bb8b-053e977d71c8/image.png)![](https://velog.velcdn.com/images/lionjojo/post/c93b8ae5-84e8-40ec-b472-8df9927ab956/image.png)

- 평균시간 : 약 828 ms
- 검색한 Row 수 : 995279 개
- 검색 타입 : Full Table Scan
### 개선 후

```sql
ALTER TABLE users ADD INDEX index_nickname (nickname);
```
- 성능 개선을 위하여 `INDEX` 기법을 사용하였다.

![](https://velog.velcdn.com/images/lionjojo/post/82e95ff9-992a-4a9e-a839-40163602f560/image.png)![](https://velog.velcdn.com/images/lionjojo/post/afe351e4-1b38-461a-9a53-d1a20e2b18c4/image.png)

- 평균 : 약 27.25 ms
- 검색한 Row 수 : 1 개
- 검색 타입 : 비고유 인덱스 스캔
- 평균 실행시간을 API단위로 따졌을때 `검색속도` 에 대한 변경사항(API 실행시간, 메모리 상태, 네트워크 딜레이 등) 이 너무많다 생각하여 `MySql EXPLAIN` 을 통해 DB 기준의 `검색 성능` 을 같이 측정하였다.

### 사용한 SQL 문
```sql
select SQL_NO_CACHE * from users where users.nickname ='구철파';
select SQL_NO_CACHE * from users where users.nickname ='오기연';
select SQL_NO_CACHE * from users where users.nickname ='황일박';
select SQL_NO_CACHE * from users where users.nickname ='홍심장';
EXPLAIN select * from users where nickname ='구철파';
EXPLAIN select * from users where nickname ='오기연';
EXPLAIN select * from users where nickname ='황일박';
EXPLAIN select * from users where nickname ='홍심장';
ALTER TABLE users ADD INDEX index_nickname (nickname);
```
---
# Level. Custom
## CI 적용
```yml
name: Run Test

# Event Trigger 특정 액션 (Push, Pull_Request)등이 명시한 Branch에서 일어나면 동작을 수행한다.
on:
  push:
    # 배열로 여러 브랜치를 넣을 수 있다.
    branches:
      - main
  # 실제 어떤 작업을 실행할지에 대한 명시
jobs:
  build:
    # 스크립트 실행 환경 (OS)
    # 배열로 선언시 개수 만큼 반복해서 실행한다. ( 예제 : 1번 실행)
    runs-on: [ ubuntu-latest ]

    # 실제 실행 스크립트
    steps:
      # uses는 github actions에서 제공하는 플러그인을 실행.(git checkout 실행)
      - name: checkout
        uses: actions/checkout@v4

      # with은 plugin 파라미터 입니다. (java 17버전 셋업)
      - name: java setup
        uses: actions/setup-java@v2
        with:
          distribution: 'adopt' # See 'Supported distributions' for available options
          java-version: '17'

      - name: make executable gradlew
        run: chmod +x ./gradlew

      # run은 사용자 지정 스크립트 실행
      - name: run unittest
        run: |
          ./gradlew clean test
```
### 테스트 성공
![](https://velog.velcdn.com/images/lionjojo/post/f306570f-b197-43e7-ab3e-2dc5ce0bd68e/image.png)
### 테스트 실패
![](https://velog.velcdn.com/images/lionjojo/post/f621cd54-d4d7-452c-a787-daa337e0f114/image.png)


- 현재 `main` 브랜치에 최소한의 안전장치를 위하여 `gradle` 이 제대로 빌드되는지와 작성된 TestCode들이 제대로 동작하는지에 대한 CI를 `GithubAction` 을 통해 지정해두었다.
- 사용자는 Push후 수행되는 CI 결과의 따라 어느부분의 문제가 있는지 혹은 테스트코드가 제대로 동작하지 않는지를 파악할 수 있다.
- 일반적으로 CI 뿐만 아니라 Main 브랜치에 PR이 되어 머지가 되었을때 배포자동화를 담당하는 CD 까지 작성하는게 일반적임으로 이후에 추가할 예정이다.

---
## Docker-Compose 를 이용한 Local DB 공통화
```yml
services:
  mysql:
    image: mysql:8.0
    container_name: spring-plus
    environment:
      MYSQL_ROOT_PASSWORD: admin
      MYSQL_DATABASE: spring-plus
      MYSQL_USER: admin
      MYSQL_PASSWORD: admin
    ports:
      - "3307:3306"
    volumes:
      - db-data:/var/lib/mysql
    networks:
      - app-network
    healthcheck:
      test: [ "CMD", "mysqladmin", "ping", "-h", "localhost" ]
      timeout: 20s
      retries: 10
    restart: always

networks:
  app-network:
    driver: bridge
volumes:
  db-data:
```
![](https://velog.velcdn.com/images/lionjojo/post/602f98ba-8ae7-4288-b3ec-904c2891e828/image.png)![](https://velog.velcdn.com/images/lionjojo/post/3ea7345d-24e0-45fb-a39e-dd94c51c17f8/image.png)

- 현재는 개인 프로젝트이기에 개발환경의 공통화가 필요가없을수도 있지만 후에 배포가되거나 다른 유저가 해당소스를 실행시킬 필요가 있을수도 있다.
- 자신의 MySql 로컬환경을 프로젝트에 맞추지않더라도 docker 를 통해서 실행이 가능하도록 설정하였다.
