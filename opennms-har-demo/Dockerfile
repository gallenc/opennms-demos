##
# Use opennms horizon selenium branch as base image.
# opennms/horizon:jira_NMS-13190
##

ARG BASE_IMAGE="opennms/horizon"
ARG BASE_IMAGE_VERSION="jira-NMS-13190"

FROM ${BASE_IMAGE}:${BASE_IMAGE_VERSION} as horizon-base

ARG GECKODRIVER_VERSION="0.29.1"
ARG GECKODRIVER_URL="https://github.com/mozilla/geckodriver/releases/download/v${GECKODRIVER_VERSION}/geckodriver-v${GECKODRIVER_VERSION}-linux64.tar.gz"

# allow root commands to build image
USER 0
SHELL ["/bin/bash", "-c"]

# install firefox without x drivers since running headless
RUN dnf install -y firefox && \
    dnf clean all && \
    rm -rf /var/cache/yum

# install gecko driver 
RUN curl --retry 5 --fail -L ${GECKODRIVER_URL} --create-dirs -o /opt/geckodriver/geckodriver.tar.gz && \
    tar -xvzf /opt/geckodriver/geckodriver* -C /opt/geckodriver/ && \
    rm /opt/geckodriver/geckodriver*.gz && \
    chown -R opennms:root /opt/geckodriver/ && \
    chmod +x /opt/geckodriver/geckodriver

### Containers should NOT run as root as a good practice - use opennms user for runs
USER 10001
