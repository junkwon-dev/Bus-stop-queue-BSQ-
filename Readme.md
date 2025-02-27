# 캡스톤 문서화(서버)

# 서버 구성도

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled.png)

---

# 서버 정보

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%201.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%201.png)

---

# 보안그룹(인바운드 규칙)

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%202.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%202.png)

---

# DB (bus_stop_db)

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%203.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%203.png)

### bus_stop (경기도 데이터드림 버스정류소 현황 sheet + 공간데이터 coords 추가)

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%204.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%204.png)

- coords 추가 이유 : 가장 가까운 정류소를 찾기 위해 ST_DISTANCE_SPHERE() 함수를 이용하는데, 이 함수는 공간좌표 point(x,y) 로만 작동 가능함.

> 라이센스 : 상업적 이용, 콘텐츠 변경 허용

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%205.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%205.png)

## user_table (사용자 중복 체크 위한 테이블)

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%206.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%206.png)

### utilizing (정류장들에 누가 있는지(장애인, 일반인) 체크하기 위한 테이블)

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%207.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%207.png)

---

# REST API 목록

## 최단거리  api

위도, 경도 줬을때 제일 가까운 5정류장 반환하는 api (JSON)

쿼리문

```php
$sql = "SELECT *, ST_DISTANCE_SPHERE(POINT({$now_lng},{$now_lat}), coords) AS dist from bus_stop order by dist limit 5;";
```

api 문서

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%208.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%208.png)

명지대학교 위도, 경도 입력했을 때(37.2217444,127.184463)의 예시 결과값

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%209.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%209.png)

## 대기열 등록/제거 api

사용자 정보, 정류장 정보 받아서 대기열에 등록/제거하는 api

쿼리문

```php
if($do_what==1){
        echo $stop_id, $do_what, $username, $ko_name, $with_wheel;
        $sql="INSERT INTO utilizing(stop_id,user_id,ko_name,with_wheel) VALUES ($stop_id,'$username','$ko_name',$with_wheel);";

}
else if($do_what==0){
        $sql="DELETE FROM utilizing WHERE user_id=\"".$username."\";";
}
```

api 문서

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%2010.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%2010.png)

## 정류장 인원 수 api

다음 정류장 stop_id 입력 시 그 정류장의 일반인, 장애인 명수를 반환하는 api (JSON)

쿼리문

```php
$sql = "SELECT count(*) as cnt, count(with_wheel) as wheel from utilizing where stop_id=".$stop_id.";";
```

api 문서

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%2011.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%2011.png)

사용 예시([http://ec2-52-193-213-157.ap-northeast-1.compute.amazonaws.com/driver/index.php?stop_id=888888999](http://ec2-52-193-213-157.ap-northeast-1.compute.amazonaws.com/driver/index.php?stop_id=888888999))

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%2012.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%2012.png)

db 확인 ( dummy 값 생성 ) 

![Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%2013.png](Untitled%201b78e029af234b4baf20c662d47b6ad6/Untitled%2013.png)