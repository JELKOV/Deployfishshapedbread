# Base image 설정 (Tomcat 사용)
FROM tomcat:10.1-jdk17

# Tomcat의 기본 webapps 디렉토리를 정리
RUN rm -rf /usr/local/tomcat/webapps/*

# WAR 파일을 ROOT로 복사 (루트 경로로 서비스)
COPY target/bungeobbangProject-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# (선택 사항) 볼륨 사용 설정 - 필요 시에만 유지
VOLUME ["/usr/local/tomcat/images"]

# 포트 노출 설정 (Tomcat의 기본 포트)
EXPOSE 8080

# (선택 사항) 컨테이너 건강 체크
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1
# (선택 사항) 크롤링 제외
ENV ENABLE_CRAWLING=false

# Tomcat 실행 명령 설정
CMD ["catalina.sh", "run"]
