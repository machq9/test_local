# 原逻辑（仅支持pull_request/push）
def handle_github_webhook():
    event = request.headers.get('X-GitHub-Event')  # 补充：从请求头获取事件类型
    # 新增：兼容ping事件
    if event == 'ping':
        logger.info("Received GitHub ping event, returning 200 OK")
        return jsonify({"status": "success", "message": "Ping received"}), 200
    # 原有校验逻辑
    if event not in ['pull_request', 'push']:
        logger.error(f"❌ Only pull_request and push events are supported for GitHub webhook, but received: {event}.")
        return jsonify({"error": "Unsupported event type"}), 400
    # 后续处理pull_request/push事件的逻辑...
