until gh run download 35986036632; do
    echo "Command failed, retrying..."
    sleep 1
done
echo "Command succeeded!"