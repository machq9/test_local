# data_processor.py
def calculate_statistics(user_list):
    """计算用户数据统计"""
    total_age = 0
    adult_count = 0
    names = []
    for user in user_list:
        if user['age'] > 18:
            adult_count +=1
        total_age += user['age']
        names.append(user['name'].strip())
    
    avg_age = total_age / len(user_list) if user_list else 0
    return {
        'average_age': avg_age,
        'adult_ratio': adult_count/len(user_list) if user_list else 0,
        'unique_names': list(set(names))
    }

def fetch_user_data(user_ids):
    """批量获取用户数据（模拟API调用）"""
    results = []
    for id in user_ids:
        # 模拟网络请求（无超时处理）
        resp = requests.get(f"https://api.example.com/users/{id}")
        results.append(resp.json())
    return results

def filter_active_users(users, min_login_days=30):
    active = []
    for u in users:
        if u.get('last_login_days') and u['last_login_days'] >= min_login_days:
            active.append(u)
    return active
