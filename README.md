## 🎮 Pac-Man
소켓 통신을 이용한 멀티 팩맨 게임
<br><br>

## 📚 Stacks
<img src="https://img.shields.io/badge/eclipseide-2C2255?style=flat&logo=eclipse&logoColor=white" />  <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Conda-Forge&logoColor=white" />
<br><br>

## 🗒️ Project Introduction
<h3> 개요 </h3>
2020년 2학기 네트워크 프로그래밍 수업 개인 프로젝트입니다.<br>
소켓 프로그래밍을 사용하여 멀티 팩맨 게임과 채팅 기능을 구현했습니다.<br>
<br>

<h3> 기능 </h3>
- 멀티 플레이 기능(최대 2명의 플레이어 참여 가능)<br>
- 점수 관리 기능<br>
- 채팅 관리 기능<br>
<br>

<h3> 게임 설명 </h3>
- 2인 플레이 게임으로써 두 명이 들어와 있어야 게임 진행이 가능하다.<br>
- 고스트를 피해 쿠키를 많이 먹는 팩맨이 승리하는 게임이다.<br>
- 고스트와 부딪치면 팩맨은 죽게 된다.<br>
<br><br>

## ⚒️ Project Design
<h3>System Configuration</h3>
<img width="60%" alt="image" src="https://user-images.githubusercontent.com/44528897/235651093-50f87049-39fb-4dd2-acbb-cebfa86c2102.png"><br><br>


<h3> Flow Control </h3>
<img width="90%" alt="image" src="https://user-images.githubusercontent.com/44528897/235650689-a6cd7360-d909-45eb-a5a9-e3f33da3a907.png"><br>


<h3> Protocol List </h3>
<!-- 
<img width="80%" alt="스크린샷 2023-05-02 오후 9 42 15" src="https://user-images.githubusercontent.com/44528897/235669238-c2f087e2-936b-44fe-bf60-8c052700f929.png"> -->


| Protocol | 용도/내용 | 방향 |
| :---: | :---: | --- |
| 100 | Login | Client → Server | 
| 200 | 채팅 Message | Client → Server → Client| 
| 400 | Logout | Client → Server | 
| 501 | Space True | Client → Server → Client | 
| 502 | Space False | Client → Server → Client | 
| 601 | KeyPressed | Client → Server → Client | 
| 602 | KeyReleased | Client → Server → Client | 
| 701 | Start(STATE) | Client → Server → Client | 
| 702 | Game(STATE) | Client → Server → Client | 
| 703 | showText False | Client → Server → Client | 
| 704 | showText True | Client → Server → Client | 
| 800 | Score | Client → Server → Client | 
| 901 | Ghost Move | Client → Server → Client | 

<br><br>
## 🕹️ 기능 소개
<h3> 시작 화면 </h3>
<img width="60%" alt="스크린샷 2023-05-02 오후 9 43 55" src="https://user-images.githubusercontent.com/44528897/235676141-dea56592-1c82-4814-afbf-0bf1faa1680f.png">
► Server를 연결한 뒤 Client를 연결했을 때 나오는 첫 로그인 화면이다. <br>
► Username을 입력하고 start 버튼을 누르면, 채팅과 게임을 같이 할 수 있는 게임 창이 나온다. <br>
<br>

<h3> 플레이 화면 1 - 플레이어 1명만 접속했을 때 </h3>
<img width="75%" alt="스크린샷 2023-05-02 오후 9 44 06" src="https://user-images.githubusercontent.com/44528897/235677406-435f171a-ec60-4ee4-8ed5-bf547bcc677f.png">
► 팩맨 게임의 경우 2인 플레이만 가능하다. <br>
► 만약 Client가 하나만 켜져 있는 상태에서 space를 눌러 게임을 시작하게 되면,<br> &nbsp &nbsp &nbsp‘시작인원이 충족되지 않았습니다.’라고 채팅창에 뜬다. <br><br>

<h3> 플레이 화면 2 - 플레이어 2명이 함께 플레이 </h3>
<img width="75%" alt="스크린샷 2023-05-02 오후 10 21 47" src="https://user-images.githubusercontent.com/44528897/235678860-62b2749e-f9cc-48c8-b6ad-26f2b88c2238.png">
► 게임 창 상단에는 각각 팩맨의 점수가 표시된다. <br>
► Player 1(노란 팩맨)은 키보드의 화살표키로 이동하고, Player 2(빨간 팩맨)는 asdw키로 이동한다. <br>
► 게임을 하면서 채팅도 이용 가능하다. <br>
► 게임 진행 중인 화면에서 게임을 재시작하고 싶을 때는, 키보드 R 버튼을 누르면 가능하다. <br><br>

<h3> 플레이 화면 3 - 게임이 끝났을 떄 </h3>
<img width="75%" alt="스크린샷 2023-05-02 오후 10 25 24" src="https://user-images.githubusercontent.com/44528897/235679660-1f0af6b3-4094-4e0d-9383-1b1096143b5a.png">
► 고스트와 부딪치거나 모든 쿠키를 먹었을 때, 게임이 끝난다. <br>
► 게임이 게임의 승자와 space를 키를 눌러 새 게임을 시작하라는 문구가 나온다. <br>
► 게임을 재 시작하는 버튼은 space로 가능하다. <br><br>

