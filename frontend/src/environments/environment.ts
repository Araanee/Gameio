// Build de PRODUCTION (ng build → déployé sur S3).
// apiBase = DNS public de l'ALB. Récupère-le via : terraform output -raw alb_dns_name
// puis remplace la valeur ci-dessous (garde le http:// et PAS de slash final).
export const environment = {
  production: true,
  apiBase: 'http://REMPLACE-PAR-TON-ALB-DNS',
};
