# Awesome Project

# 구현 과제
* [https://www.numble.it/deepdive/33](https://www.numble.it/deepdive/851a2dc7-8bc1-4c11-97f8-aa6ed1bd624f)

# 모델 설계도

* ![awesome_project_model.jpeg](/mockup-1.jpeg)

# 시스템 설계도

* ![awesome_system.jpeg](/system.jpeg)

* 폴더의 경우 Object Storage에 업로드 되지 않고 오로지 File류만 Object Storage에 업로드 되도록 구현되어 있음

# 구현 API 목록

* 호스트 정보
    * ~~`110.165.16.112:8081`~~
* ~~테스트시 포스트맨을 활용할 것~~
* 현재 서버 닫혀있음

## Auth

### 회원가입

#### Request

`POST /auth/register`

#### Request Header

* `Content-Type: application/json`

#### Request Body

```json
{
  "username": "test",
  "password": "1234"
}
```

#### Response

```json
{
  "id": "641ff2b95e5b2a0930372751",
  "username": "test",
  "rootStorageId": "641ff2b95e5b2a0930372752",
  "storageFileSize": 0,
  "createdAt": "2023-03-26T16:22:33.520036",
  "updatedAt": "2023-03-26T16:22:33.520036"
}
```

* 회원가입 후 해당 유저 정보를 반환합니다.
* 최상위 스토리지 Id를 같이 반환합니다.

### 로그인

#### Request

`POST /auth/login`

#### Request Header

* `Content-Type: application/json`

#### Request Body

```json
{
  "username": "test",
  "password": "1234"
}
```

#### Response

```json
{
  "success": true,
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhY2Nlc3NUb2tlbiIsInVpZCI6IjY0MWZmMmI5NWU1YjJhMDkzMDM3Mjc1MSIsImlhdCI6MTY3OTgxNTM2MiwiZXhwIjoxNjc5OTAxNzYyfQ.getxUN9WPqLcVlSyy8BQ5uAiVQTvSn5Jn4KRBZN8_eU",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyZWZyZXNoVG9rZW4iLCJ1aWQiOiI2NDFmZjJiOTVlNWIyYTA5MzAzNzI3NTEiLCJpYXQiOjE2Nzk4MTUzNjIsImV4cCI6MTY3OTkwMTc2Mn0.z_fEy03z6xm3I07m8z8yUAlKcSwN9hzxLUGyDB5iK0M"
}
```

* 로그인 성공 여부와 토큰을 반환합니다.
* 유저 API, 스토리지 API를 호출할 때 `accessToken`을 반드시 `Authorization` 헤더에 넣어서 호출해야 합니다.

## User

### 유저 정보 확인

#### Request

`GET /user/user-info`

#### Request Header

* `Authorization: {ACCESS_TOKEN}`

#### Response

```json
{
  "id": "641ff2b95e5b2a0930372751",
  "username": "test",
  "rootStorageId": "641ff2b95e5b2a0930372752",
  "storageFileSize": 1675,
  "createdAt": "2023-03-26T16:22:33.52",
  "updatedAt": "2023-03-26T16:22:33.52"
}
```

* 해당 유저의 정보를 반환합니다.
    * 최상위 스토리지의 id를 같이 반환합니다.
* 현재 사용중인 스토리지 용량도 같이 반환합니다.

## Storage

### 1. 스토리지 정보 확인

#### Request

`GET /storage/{storageId}`

#### Request Header

* `Authorization: {ACCESS_TOKEN}`

#### Response

```json
{
  "id": "641ff2b95e5b2a0930372752",
  "parentStorageId": null,
  "storageName": "root::641ff2b95e5b2a0930372751",
  "storageFileSize": 1675,
  "extType": "FOLDER",
  "createdAt": "2023-03-26T16:22:33.621",
  "updatedAt": "2023-03-26T16:22:33.621"
}
```

* 해당 스토리지의 정보를 반환합니다.

### 2. 스토리지 정보 내부 리스트 확인

#### Request

`GET /storage/{storageId}/hierarchy`

#### Request Header

* `Authorization: {ACCESS_TOKEN}`

#### Response

```json
[
  {
    "id": "641ff3785e5b2a0930372753",
    "parentStorageId": "641ff2b95e5b2a0930372752",
    "storageName": "rational331.pem",
    "storageFileSize": 1675,
    "extType": "FILE",
    "createdAt": "2023-03-26T16:25:44.07",
    "updatedAt": "2023-03-26T16:25:44.07"
  },
  {
    "id": "641ff3915e5b2a0930372754",
    "parentStorageId": "641ff2b95e5b2a0930372752",
    "storageName": "test",
    "storageFileSize": 0,
    "extType": "FOLDER",
    "createdAt": "2023-03-26T16:26:09.98",
    "updatedAt": "2023-03-26T16:26:09.98"
  }
]
```

* 해당 스토리지의 하위 폴더와 파일 리스트를 반환합니다.

### 3. 폴더 만들기

#### Request

`POST /storage/new-folder/{storageId}/{folderName}`

* `storageId`의 해당하는 storage에 `folderName`라는 이름으로 새롭게 폴더를 추가합니다.

#### Request Header

* `Authorization: {ACCESS_TOKEN}`

#### Response

```json
{
  "id": "641ff3915e5b2a0930372754",
  "parentStorageId": "641ff2b95e5b2a0930372752",
  "storageName": "test",
  "storageFileSize": 0,
  "extType": "FOLDER",
  "createdAt": "2023-03-26T16:26:09.980942",
  "updatedAt": "2023-03-26T16:26:09.980942"
}
```

* 생성된 폴더 정보를 반환합니다.

### 4. 파일 업로드

#### Request

`POST /storage/{storageId}`

#### Request Header

* `Authorization: {ACCESS_TOKEN}`
* `Content-Type: multipart/form-data`

#### Request Body

| key  |   value    |
|:----:|:----------:|
| file | LOCAL_FILE |

* ![upload.jpeg](/img_1.png)

#### Response

```json
{
  "id": "641ff3785e5b2a0930372753",
  "parentStorageId": "641ff2b95e5b2a0930372752",
  "storageName": "rational331.pem",
  "storageFileSize": 1675,
  "extType": "FILE",
  "createdAt": "2023-03-26T16:25:44.070968",
  "updatedAt": "2023-03-26T16:25:44.070968"
}
```

* 업로드된 파일 정보를 반환합니다.

### 5. 파일 다운로드

#### Request

`GET /storage/{storageId}/download`

#### Request Header

* `Authorization: {ACCESS_TOKEN}`

#### Response

* ![download.jpeg](/img.png)
* 파일을 OCTET-STREAM 형태로 반환합니다.
* 포스트맨에서는 Send and Download 버튼을 눌러 다운로드 받을 수 있습니다.

### 6. 파일/폴더 삭제하기

#### Request

`DELETE /storage/{storageId}`

#### Request Header

* `Authorization: {ACCESS_TOKEN}`

#### Response

```json
true
```

* 폴더를 삭제한다면 해당 폴더와 그 하위의 모든 폴더와 파일을 삭제합니다.
* 파일을 삭제한다면 해당 파일을 삭제합니다.
* 삭제 성공 여부를 반환합니다.

# 미구현 기능

* 폴더 다운로드 기능
* 테스트가 부족함
* 성능 부하 테스트 X
* 프론트가 없어서 테스트가 불편함

# 참고 사항

* CI/CD는 ~~`110.165.16.112:8080`~~ 젠킨스쪽에서 이루어짐
   * 현재 서버 닫혀있음
* main 브랜치에 푸시가 발생하면 배포까지 자동으로 이루어짐

```shell
echo "PID Check..."

CURRENT_PID=$(ps -ef | grep java | grep awesome | awk '{print $2}')

echo "Running PID: {$CURRENT_PID}"
if [ -z $CURRENT_PID ]; then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -9 $CURRENT_PID"
  kill -9 $CURRENT_PID
sleep 10
fi

echo "Deploy Project...."

nohup java -Djasypt.encryptor.password=rational31 -Dspring.profiles.active=real -jar /var/lib/jenkins/workspace/awesome/build/libs/AwesomeProject-0.0.1-SNAPSHOT.jar &
```

# 느낀점

* 웹플럭스를 활용해볼 수 있다는 점이 매력적이었음
* 파일을 업로드하였을 때 재귀적으로 부모 스토리지 용량을 업데이트하는 부분을 깔끔하게 하는 부분이 개인적으로 마음에 들었음
* 마찬가지로 파일/폴더를 삭제할 때도 재귀적으로 부모 스토리지 용량을 업데이트하는 부분이 개인적으로 마음에 들었음
* 그 이외에도 파일을 업로드, 다운로드, 삭제할 때 코드가 깔끔한게 마음에 들었음
* CI/CD를 처음부터 구축하는 부분이 배울점이 많았음
* 테스트를 많이 작성하지 못해서 아쉬움
* 성능 테스트를 하지 못해서 아쉬움
* 프론트가 없어서 테스트가 불편함
* https://rational331.github.io/my%20box%20challenge/my-box-first-week/
* https://rational331.github.io/my%20box%20challenge/my-box-second-week/
