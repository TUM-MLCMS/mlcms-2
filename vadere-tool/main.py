import json
import os

def load_scenario(filename):
    with open(filename) as json_file:
        scenario = json.load(json_file)
        return scenario

def save_scenario(scenario, filename):
    with open(filename, 'w') as outfile:
        json.dump(scenario, outfile)

def add_pedestrian(scenario, x, y):
    with open("template_pedestrian.json") as json_file:
        pedestrian = json.load(json_file)
        pedestrian["position"]["x"] = x
        pedestrian["position"]["y"] = y
        for target in scenario["scenario"]["topography"]["targets"]:
            pedestrian["targetIds"].append(target["id"])
        scenario["scenario"]["topography"]["dynamicElements"].append(pedestrian)


scenario = load_scenario("../scenarios/Task 1.3/Rimea_6_Corner.scenario")
add_pedestrian(scenario, 11.5, 1.5)
save_scenario(scenario, "output.scenario")