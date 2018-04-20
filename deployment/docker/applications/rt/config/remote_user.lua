local my_local_remote_user = lighty.request["RT_REMOTE_USER"]
lighty.req_env["REMOTE_USER"] = my_local_remote_user
