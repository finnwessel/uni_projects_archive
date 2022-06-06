using UnityEngine;

public class OfficerTrigger : MonoBehaviour {
    public float letOffTimer;
    private float _letOffTime;
    private WaypointController officer;
    private float _smallestDistance = 1000;
    private float _distanceCheck;
    
    private bool _playerOutsideTrigger = false;
    void Start() {
        officer = transform.parent.GetComponent<WaypointController>();
        _letOffTime = letOffTimer;
    }

    private void setNearestWaypoint() {
        for (int i = 0; i <= officer.waypoint.Count - 1; i++) {
            _distanceCheck = Vector3.Distance(officer.waypoint[i].position, transform.position);
            if (_distanceCheck <= _smallestDistance) {
                _smallestDistance = _distanceCheck;
                officer._targetWaypoint = i + 1;
            } 
        }
    }

    private void checkEndOfPath() {
        if (officer._targetWaypoint >= officer.waypoint.Count) {
            officer._targetWaypoint = 0;
        }
    }
    
    private void OnTriggerEnter(Collider other) {
        if (other.gameObject.name == "Player") {
            officer.PlayerInRange = true;
            _playerOutsideTrigger = false;
        }
    }

    private void OnTriggerExit(Collider other) {
        if (other.gameObject.name == "Player") {
            setNearestWaypoint();
            checkEndOfPath();
            _smallestDistance = 1000;
            letOffTimer = _letOffTime;
            _playerOutsideTrigger = true;
        }
    }

    private void Update() {
        if (_playerOutsideTrigger) {
            if (letOffTimer >= 0) {
                letOffTimer -= Time.deltaTime;
            } else {
                officer.PlayerInRange = false;
                _playerOutsideTrigger = false;
            }
        }
    }
}
