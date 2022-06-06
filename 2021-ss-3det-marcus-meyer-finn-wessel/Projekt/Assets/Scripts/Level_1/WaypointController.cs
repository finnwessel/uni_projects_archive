using System;
using System.Collections.Generic;
using UnityEngine;

public class WaypointController : MonoBehaviour {
    public List<Transform> waypoint = new List<Transform>();
    public float MovementSpeed;
    public float ChaseSpeed;
    public bool PlayerInRange = false;
    public int _targetWaypoint = 0;
    public float y_Offset = 2f;
    private float[] _timeToWait;
    private Transform _player;
    private Quaternion _origRotation;
    private Quaternion _newRotation;
    private float _waitingTimer = 0;

    private void Start() {
        _player = GameObject.Find("Player").transform;
        _timeToWait = new float[waypoint.Count];
        for (int i = 0; i <= waypoint.Count - 1; i++) {
            _timeToWait[i] = waypoint[i].GetComponent<WaypointSettings>().timeToWait;
        }
    }

    private void OnCollisionEnter(Collision other) {
        if (other.gameObject.name == "Player") {
            GameManager.Instance.Lost("BUSTED");
        }
    }

    private void moveToNextWaypoint() {
        smoothRotationTowards(waypoint[_targetWaypoint], 3);
        transform.position = Vector3.MoveTowards(transform.position, waypoint[_targetWaypoint].position, MovementSpeed * Time.deltaTime);
    }

    private void checkDistanceAndSetNextWaypoint() {
        if (Vector3.Distance(transform.position, waypoint[_targetWaypoint].position) <= 0.5) {
            if (_targetWaypoint >= waypoint.Count - 1) {
                _targetWaypoint = 0;
            } else {
                _waitingTimer = _timeToWait[_targetWaypoint++];
            }
        } 
    }

    private void moveToPlayer() {
        smoothRotationTowards(_player, 5);
        transform.position = Vector3.MoveTowards(transform.position, new Vector3(_player.position.x, _player.position.y + y_Offset, _player.position.z), MovementSpeed * ChaseSpeed * Time.deltaTime);
    }

    private void smoothRotationTowards(Transform target, float speed) {
        _origRotation = transform.rotation;
        transform.LookAt(target);
        _newRotation = transform.rotation;
        transform.rotation = _origRotation;
        transform.rotation = Quaternion.Lerp(transform.rotation, _newRotation, Time.deltaTime * speed);
    }

    void Update() {
        if (PlayerInRange) {
            moveToPlayer();
        } else {
            if (_waitingTimer >= 0) {
                _waitingTimer -= Time.deltaTime;
            } else {
                moveToNextWaypoint();
                checkDistanceAndSetNextWaypoint();
            }
        }
    }

    private void OnDrawGizmos() {
        for (int i = 0; i < waypoint.Count; i++) {
            Gizmos.color = Color.magenta;
            Gizmos.DrawSphere(waypoint[i].position, 1);
            Gizmos.color = Color.red;
            if (i == waypoint.Count - 1) {
                Gizmos.DrawLine(waypoint[i].position, waypoint[0].position);
            } else {
                Gizmos.DrawLine(waypoint[i].position, waypoint[i + 1].position);
            }
        }
    }
}
