#!/usr/bin/env python3
import argparse
from typing import List, Dict

STRACE_MARK = "==================Stack Trace=================="
BREAK_MARK = "---------------------------------------"

class Pair:
    def __init__(self, fst: List[str], snd: List[str]):
        self.fst = "\n".join(fst)
        self.snd = "\n".join(snd)

    def __eq__(self, other):
        if not isinstance(other, Pair):
            return False
        case1 = self.fst == other.fst and self.snd == other.snd
        case2 = self.fst == other.snd and self.snd == other.fst
        return case1 or case2

    def __hash__(self):
        return hash((self.fst, self.snd))

    def __str__(self):
        return f"{self.fst}\n-\n{self.snd}\n"

    def __repr__(self):
        return self.__str__()

def get_pairs(racy_pairs_file: str) -> Dict[Pair, int]:
    pairs = set()
    counts = {}
    reading_first = True
    first_strace = []
    second_strace = []
    with open(racy_pairs_file) as pair_file:
        while (line := pair_file.readline()):
            if STRACE_MARK in line and "\t" not in line:
                reading_first = True
            elif STRACE_MARK in line and "\t" in line:
                reading_first = False
            else:
                continue

            if reading_first:
                first_strace = []
                while (tmp_line := pair_file.readline()):
                    if "|" in tmp_line:
                        break
                    first_strace.append(tmp_line.strip())
            else:
                second_strace = []
                while (tmp_line := pair_file.readline()):
                    if "|" in tmp_line:
                        break
                    second_strace.append(tmp_line.strip())
                new_pair = Pair(first_strace, second_strace)
                if new_pair in pairs:
                    counts[new_pair] += 1
                else:
                    pairs.add(new_pair)
                    counts[new_pair] = 1
    return counts

def write_results(output_file: str, counts: Dict[Pair, int]):
    with open(output_file, "w") as f:
        for pair, count in counts.items():
            f.write(BREAK_MARK + "\n")
            f.write(str(pair) + "\n")
            f.write(f"Count: {count}\n")

def main(args):
    counts = get_pairs(args.racy_pairs_file)
    write_results(args.output_file, counts)

if __name__ == "__main__":
    arg_parser = argparse.ArgumentParser(description='Cluster Racy Pairs')
    arg_parser.add_argument('racy_pairs_file', type=str, help='The racy pairs file to cluster')
    arg_parser.add_argument('output_file', type=str, help='The output file to write the clustered racy pairs to')
    args = arg_parser.parse_args()
    main(args)
