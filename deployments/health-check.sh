echo "**********************************************************************"
echo "*                                                                    *"
echo "*           LangReg Service HEALTH CHECK                             *"
echo "*                                                                    *"
echo "**********************************************************************"


HEALTH_ENDPOINTS=(
  "http://localhost:8081/actuator/health"
  "http://localhost:8082/management/health"
  "http://localhost:8083/management/health"
)
ALL_SERVICES_UP=true
for ENDPOINT in "${HEALTH_ENDPOINTS[@]}"; do
  RESPONSE=$(curl -s $ENDPOINT | grep '"status":"UP"')
  if [ -z "$RESPONSE" ]; then
    ALL_SERVICES_UP=false
    echo "===> Service à l'endpoint $ENDPOINT n'est pas UP."
  else
    echo "===> Service à l'endpoint $ENDPOINT est UP."
  fi
done


if $ALL_SERVICES_UP; then
  echo "===> Tous les services sont opérationnels."
else
  echo "===> Une ou plusieurs erreurs détectées dans les services."
fi


echo "**********************************************************************"
echo "*                                                                    *"
echo "*                               FIN                                  *"
echo "*                                                                    *"
echo "**********************************************************************"