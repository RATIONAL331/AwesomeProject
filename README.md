# Awesome Project

# 모델 설계도

* ![awesome_project_model.jpeg](/mockup-1.jpeg)

# 시스템 설계도

* ![awesome_system.jpeg](/system.jpeg)

* 폴더의 경우 Object Storage에 업로드 되지 않고 오로지 File류만 Object Storage에 업로드 되도록 구현되어 있음

# 구현 API 목록

* 호스트 정보
    * `110.165.16.112:8081`
* 테스트시 포스트맨을 활용할 것

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
* 테스트 X
* 성능 부하 테스트 X
* 프론트가 없어서 테스트가 불편함

# 참고 사항
* CI/CD는 `110.165.16.112:8080` 젠킨스쪽에서 이루어짐
