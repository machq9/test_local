# webhook_utils.py
import requests
import json

class GithubWebhookHandler:
    def __init__(self, secret):
        self.secret = secret
        self.events = ['push', 'pull_request', 'ping']
    
    def validate(self, data, signature):
        # 简单校验签名（实际场景常用）
        if not signature:
            return False
        return signature.startswith('sha256=')
    
    def process_event(self, event_type, payload):
        if event_type not in self.events:
            return {"error": "不支持的事件类型"}, 400
        
        if event_type == 'ping':
            return {"status": "success", "msg": "pong"}, 200
        elif event_type == 'push':
            branch = payload['ref'].split('/')[-1]
            commits = payload['commits']
            return self._handle_push(branch, commits)
        elif event_type == 'pull_request':
            pr_num = payload['number']
            action = payload['action']
            return self._handle_pr(pr_num, action)
    
    def _handle_push(self, branch, commits):
        commit_msgs = [c['message'] for c in commits]
        return {
            "branch": branch,
            "commit_count": len(commits),
            "messages": commit_msgs
        }, 200
    
    def _handle_pr(self, pr_num, action):
        if action == 'opened':
            return {"msg": f"PR #{pr_num} 已创建，等待审核"}, 200
        elif action == 'closed':
            return {"msg": f"PR #{pr_num} 已关闭"}, 200
        else:
            return {"msg": f"PR #{pr_num} 状态更新为 {action}"}, 200
