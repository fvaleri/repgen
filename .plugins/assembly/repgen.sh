#!/usr/bin/env bash
set -Eeuo pipefail
error() {
  echo "$@" 1>&2 && exit 1
}
for x in sed java; do
  if ! command -v "$x" &>/dev/null; then
    error "Missing required utility: $x"
  fi
done

# sed non-printable text delimiter
SD=$(echo -en "\001") && readonly SD

HOME_DIR="" && pushd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")" >/dev/null \
  && { HOME_DIR="$PWD/.."; popd >/dev/null; } && readonly HOME_DIR

start() {
  echo "Starting"
  mkdir -p "$HOME_DIR"/data
  local config="$HOME_DIR/etc/application.properties"
  sed -i "s${SD}templateHome = .*${SD}templateHome = $HOME_DIR/etc/templates${SD}g" "$config"
  sed -i "s${SD}databaseHome = .*${SD}databaseHome = $HOME_DIR/data/h2/repgen${SD}g" "$config"
  nohup java -Xms5g -Xmx5g -XX:+UseG1GC -Djava.security.egd=file:/dev/urandom -DlogsDir="$HOME_DIR"/data/logs \
    -jar "$HOME_DIR"/lib/repgen-*.jar --spring.config.location="$config" >/dev/null 2>&1 &
  echo $! > "$HOME_DIR"/data/repgen.pid
  echo "Started"
}

stop() {
  echo "Stopping"
  kill -TERM "$(cat "$HOME_DIR"/data/repgen.pid)"
  echo "Stopped"
}

readonly USAGE="
Usage: $0 [command]

  start  Start the server
  stop   Stop the server
"
readonly COMMAND="${1-}"
if [[ -z "$COMMAND" ]]; then
  error "$USAGE"
else
  if (declare -F "$COMMAND" >/dev/null); then
    "$COMMAND"
  else
    error "Invalid command"
  fi
fi
