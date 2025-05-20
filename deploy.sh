#!/bin/bash

# CONFIGURATION
PROJECT_ID="lordjoehrd"
IMAGE_NAME="eastside-app"
SERVICE_NAME="eastside-resource-guide"
REGION="us-central1"

# Generate a version tag based on timestamp
#VERSION_TAG="v$(date +%Y%m%d-%H%M%S)"
VERSION_TAG="v2"
FULL_IMAGE="gcr.io/${PROJECT_ID}/${IMAGE_NAME}:${VERSION_TAG}"

# Step 1: Build the Docker image with the version tag
docker build -t ${FULL_IMAGE} .

# Step 2: Push to GCR
docker push ${FULL_IMAGE}

# Step 3: Deploy to Cloud Run using the new version
gcloud run deploy ${SERVICE_NAME} \
  --image ${FULL_IMAGE} \
  --platform managed \
  --region ${REGION} \
  --allow-unauthenticated

echo "✅ Deployed version: ${VERSION_TAG}"
