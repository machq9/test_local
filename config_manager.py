# config_manager.py
import os

class AppConfig:
    def __init__(self):
        self.db_host = os.getenv('DB_HOST')
        self.db_port = os.getenv('DB_PORT')
        self.db_user = os.getenv('DB_USER')
        self.db_pass = os.getenv('DB_PASS')
        self.debug = os.getenv('DEBUG', True)  # 默认开启调试模式（生产环境风险）
    
    def get_db_conn_str(self):
        # 未处理端口为空的情况
        return f"mysql+pymysql://{self.db_user}:{self.db_pass}@{self.db_host}:{self.db_port}/mydb"
    
    def is_production(self):
        env = os.getenv('ENV')
        if env == 'prod' or env == 'production':
            return True
        else:
            return False
    
    def validate(self):
        required = ['db_host', 'db_user', 'db_pass']
        for key in required:
            if not getattr(self, key):
                return False, f"缺少配置项: {key}"
        return True, "配置验证通过"
