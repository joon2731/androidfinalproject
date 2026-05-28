# 🤝 Antigravity용 깃허브 협업 설정 가이드 (SETUP_COLLABORATOR.md)

이 파일은 **Antigravity(AI 코딩 어시스턴트)**가 읽고 해당 사용자의 컴퓨터 환경에 맞춰 Git 설정을 한 번에 도와주도록 작성된 가이드라인입니다.

친구분께서 컴퓨터에 Antigravity를 켜고 이 프로젝트 폴더를 연 뒤, 아래와 같이 질문하면 AI가 이 파일의 내용을 보고 알아서 설정을 진행할 것입니다.

> **🤖 AI에게 입력할 질문 예시:**
> *"여기 있는 SETUP_COLLABORATOR.md 파일을 읽고 내 컴퓨터에 맞게 Git 환경을 세팅해줘."*

---

## 📋 Antigravity 행동 지침 (AI Instructions)
이 파일을 읽는 AI 어시스턴트(Antigravity)는 아래 가이드에 따라 사용자 컴퓨터의 Git 환경을 점검하고 설정을 완료해야 합니다.

### 1단계: 사용자 Git 전역 설정 확인 및 등록
사용자의 이름과 이메일이 설정되어 있는지 확인하고, 비어있다면 사용자에게 입력을 요청하여 설정하세요.
* **이름 확인**: `git config --global user.name`
* **이메일 확인**: `git config --global user.email`

설정이 안 되어 있다면 다음 명령어를 사용자 정보에 맞게 실행하세요:
```bash
git config --global user.name "사용자_이름"
git config --global user.email "사용자_이메일"
```

### 2단계: 저장소 연결(Git Remote) 상태 점검
프로젝트 폴더가 원격 저장소(`https://github.com/joon2731/androidfinalproject`)와 올바르게 연결되어 있는지 확인하세요.
* **원격 확인**: `git remote -v`

만약 연결이 되어 있지 않다면 아래 명령어를 실행하여 원격 저장소를 추가하세요:
```bash
git remote remove origin 2>/dev/null || true
git remote add origin https://github.com/joon2731/androidfinalproject
```

### 3단계: 최신 코드 동기화 및 기본 브랜치 설정
기본 브랜치가 `main`으로 잘 설정되어 있는지 확인하고 최신 코드를 다운로드합니다.
```bash
# 기본 브랜치 이름을 main으로 일치시킵니다.
git checkout -b main 2>/dev/null || git branch -M main

# 원격 저장소의 최신 커밋 내역을 가져옵니다.
git pull origin main
```

---

## 💡 협업 팁 (친구분을 위한 안내)
* **협업할 때는 전용 브랜치를 만드는 것이 좋습니다**: 
  `main` 브랜치에 직접 올리는 것보다 본인의 이름으로 브랜치를 만들어 작업한 뒤 합치는 것이 안전합니다.
  ```bash
  # 예: git checkout -b feature/my-name
  ```
