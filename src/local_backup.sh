#!/bin/bash

# Local PostgreSQL Backup and Restore Script

# CONFIGURE
DB_NAME="viva"
DB_USER="postgres"
BACKUP_FILE="backup_local.dump"

# Backup
backup() {
    echo "Backing up local DB '$DB_NAME' to $BACKUP_FILE"
    pg_dump -U "$DB_USER" -F c -f "$BACKUP_FILE" "$DB_NAME"
}

# Restore
restore() {
    echo "Restoring local DB from $BACKUP_FILE"
    createdb -U "$DB_USER" "${DB_NAME}_restored"
    pg_restore -U "$DB_USER" -d "${DB_NAME}_restored" "$BACKUP_FILE"
}

case "$1" in
    backup)
        backup
        ;;
    restore)
        restore
        ;;
    *)
        echo "Usage: $0 {backup|restore}"
        ;;
esac
