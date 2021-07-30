import hashlib

BLOCK_SIZE = 8192


def run(context):
    with open(context.my_path, "rb") as f:
        file_hash = hashlib.sha512()
        chunk = f.read(BLOCK_SIZE)
        while chunk:
            file_hash.update(chunk)
            chunk = f.read(BLOCK_SIZE)
    return file_hash.hexdigest()
