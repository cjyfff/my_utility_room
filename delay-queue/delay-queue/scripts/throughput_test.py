import requests
import uuid

URL = 'http://192.168.43.10:8889/dq/acceptMsg'


def main():

    for i in range(50000):
        data = {
            "taskId": uuid.uuid4().hex,
            "functionName": "exampleHandler", "params": "{}",
            "retryCount": 0,
            "retryInterval": 0,
            "delayTime": 3600
        }

        requests.post(URL, json=data)


if __name__ == '__main__':
    main()
