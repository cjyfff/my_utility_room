import requests
import uuid

URL = 'http://127.0.0.1:8889/dq/acceptMsg'


def main():

    for i in range(1000):
        data = {
            "taskId": uuid.uuid4().hex,
            "functionName": "exampleHandler", "params": "{}",
            "retryCount": 0,
            "retryInterval": 0,
            "delayTime": 200
        }

        requests.post(URL, json=data)


if __name__ == '__main__':
    main()
