import requests

URL = ''


def main():
    data = {
        "taskId": "qweretretgfgtytrytg99",
        "functionName": "exampleHandler","params": "{}",
        "retryCount": 0,
        "retryInterval": 0,
        "delayTime": 100
    }

    requests.post(URL, json=data)


if __name__ == '__main__':
    main()
