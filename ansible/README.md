# ansible/ — Provisionnement de l'EC2 monitoring

Ansible configure une **instance EC2 dédiée au monitoring** (créée par Terraform dans `infra/`),
avec **Grafana** + l'**agent CloudWatch**.

## Rôle
- L'EC2 est *créée* par Terraform (AMI, subnet, security group, LabRole).
- Ansible *configure* ce qui tourne dessus (idempotent, rejouable) :
  - Agent CloudWatch (collecte métriques/logs → CloudWatch).
  - Grafana (dashboards, datasource CloudWatch).

## Layout prévu
```
ansible/
├── ansible.cfg          # config (inventory, user, clé SSH)
├── inventory.ini        # généré/rempli avec l'IP publique de l'EC2 (NON committé si IP réelle)
├── inventory.ini.example
├── playbook.yml         # point d'entrée
└── roles/
    ├── cloudwatch_agent/
    └── grafana/
```

## Workflow
```bash
cd ansible
# Remplir inventory.ini avec l'IP de l'EC2 (sortie Terraform) + la clé SSH du lab
ansible -i inventory.ini all -m ping     # vérifier la connexion
ansible-playbook -i inventory.ini playbook.yml
```

> ⚠️ Learner Lab : la clé SSH (`labsuser.pem`) et l'IP changent à chaque session.
> Ne jamais committer la clé ni l'`inventory.ini` rempli avec une IP réelle.
