using System;
using System.Collections.Generic;
using System.Linq;
using DefaultNamespace;
using UnityEngine.InputSystem;
using UnityEngine;

public class CameraController : MonoBehaviour {
    private Rigidbody _player;
    private Vector3 _offset;
    private Vector3 _staticOffset;
    private Vector3 _followOffset;
    private float _currentZoom;
    private Vector3 velocity = Vector3.zero;
    private bool _rightMouseDown;
    private Vector3 nextCameraPosition;
    private HudController _hud;
    
    // for objects in line of sight detection
    private List<MeshRenderer> _currentRenderer = new List<MeshRenderer>();
    private List<MeshRenderer> _transparentRenderer = new List<MeshRenderer>();
    private RaycastHit[] crossHits = new RaycastHit[0];
    
    
    [Range(0, -360)]
    public float CameraAngleY;

    public int CameraOffsetX;
    public int CameraOffsetY;
    public int CameraOffsetZ;
    [Range(8, 40)]
    public float DefaultZoom;
    public float ZoomMax;
    public float ZoomMin;
    public float ZoomSteps;
    public float RotationSpeed;
    [Range(0f, 0.5f)]
    public float Smoothness;
    public bool ClampY;

    void Start() {
        _player = GameObject.Find("Player").GetComponent<Rigidbody>();
        _currentZoom = DefaultZoom;
        //_offset = _player.position - new Vector3(90, CameraAngleY,45) - _player.position;
        _offset = new Vector3(_player.position.x + CameraOffsetX, _player.position.y + CameraOffsetY, _player.position.z + CameraOffsetZ);
        _staticOffset = _offset;
        _hud = GameObject.Find("HUD").GetComponent<HudController>();
    }
    
    public void OnRotateToggle(InputAction.CallbackContext context) {
        if (GameManager.Instance.CameraMode == CameraMode.FREE) {
            _rightMouseDown = context.ReadValue<float>() == 1;
        }
    }
    
    public void PauseGame(InputAction.CallbackContext context) {
        if (context.performed) {
            GameManager.Instance.Pause();
        }
    }
    
    public void OnRotate(InputAction.CallbackContext context) {
        // clearing mouse delta to prevent death spin
        Vector2 _mouseDelta = _rightMouseDown ? context.ReadValue<Vector2>() : Vector2.zero;
        
        // camera rotation control
        Vector3 normalized = (transform.position - _player.position).normalized;
        Quaternion _rotationTarget = Quaternion.Euler(_mouseDelta.y * Time.deltaTime * RotationSpeed, _mouseDelta.x * Time.deltaTime * RotationSpeed, _mouseDelta.y * Time.deltaTime * RotationSpeed);
        
        // flip target angles relative to viewing angle for consistent controlling
        if (normalized.z >= -1 && normalized.z <= 0 && normalized.x < 0) {
            _rotationTarget.x *= -1;
        } else if (normalized.z >= 0 && normalized.z <= 1 && normalized.x > 0) {
            _rotationTarget.z *= -1;
        } else if (normalized.z >= -1 && normalized.z <= 0 && normalized.x > 0) {
           _rotationTarget.x *= -1;
           _rotationTarget.z *= -1;
        }
        
        // update offset to the new target
        _offset = _rotationTarget * _offset;
    }
    
    public void OnZoom(InputAction.CallbackContext context) {
        _currentZoom = Mathf.Clamp(_currentZoom - context.ReadValue<Vector2>().y, ZoomMin, ZoomMax);
    }
    
    public void OnCameraSwitch(InputAction.CallbackContext context) {
        if (context.performed) {
            GameManager.Instance.SwitchCameraMode();
            String text;
            switch (GameManager.Instance.CameraMode) {
                case CameraMode.FREE:
                    text = "FREE";
                    break;
                case CameraMode.STATIC:
                    text = "STATIC";
                    break;
                default:
                    text = "";
                    break;
            }
            _hud.ShowToast(text,2f);
        }
    }

    private void determineNextCameraPosition() {
        switch (GameManager.Instance.CameraMode) {
            case CameraMode.FREE:
                nextCameraPosition = _player.position + _offset * (_currentZoom / ZoomSteps);
                break;
            case CameraMode.STATIC:
                nextCameraPosition = _player.position + _staticOffset * (_currentZoom / ZoomSteps);
                break;
        }
    }
    
    private void setNextCameraPosition() {
        transform.position = Vector3.SmoothDamp(transform.position, target: nextCameraPosition, ref velocity, Smoothness);
        if (ClampY) {
            float clampedY = Mathf.Clamp(transform.position.y, _player.position.y - 1, _player.position.y + 20);
            transform.position = new Vector3(transform.position.x, clampedY, transform.position.z);
        }
        transform.LookAt(_player.transform);
    }

    private void getObjectsInLineOfSight() {
        crossHits = Physics.SphereCastAll(transform.position, 2f, _player.position - transform.position,
            Vector3.Magnitude(transform.position - _player.position) - _player.transform.localScale.x - 1f);
    }
    
    private void setObjectsTransparent() {
        foreach (RaycastHit hit in crossHits) {
            MeshRenderer m;
            if (hit.collider.TryGetComponent(out m)) {
                _currentRenderer.Add(m);
                if (!_transparentRenderer.Contains(m)) {
                    _transparentRenderer.Add(m);
                    foreach (Material material in m.materials) {
                        if (!material.Equals(null)) {
                            if (material.HasProperty(Shader.PropertyToID("_Color"))) {
                                Color color = new Color(material.color.r, material.color.g, material.color.b, 0.5f);
                                material.SetFloat("_Surface", 1);
                                material.SetFloat("SrcBlend", (int)UnityEngine.Rendering.BlendMode.One);
                                material.SetInt("_DstBlend", (int)UnityEngine.Rendering.BlendMode.OneMinusSrcAlpha);
                                material.SetInt("_ZWrite", 0);
                                material.SetColor("_BaseColor", color);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setObjectsNotTransparent() {
        foreach (MeshRenderer m in _transparentRenderer.ToList()) {
            if (!m.Equals(null)) {
                if (!_currentRenderer.Contains(m)) {
                    foreach (Material material in m.materials) {
                        if (material.HasProperty(Shader.PropertyToID("_Color"))) {
                            Color color = new Color(material.color.r, material.color.g, material.color.b, 1f);
                            material.SetColor("_BaseColor", color);
                            material.SetFloat("_Surface", 0);
                            material.SetInt("_DstBlend", 0);
                            material.SetInt("_ZWrite", 1);
                        }
                    }

                    _transparentRenderer.Remove(m);
                }
            }
        }
        _currentRenderer.Clear();
    }
    
    private void Update() {
        determineNextCameraPosition();
        setNextCameraPosition();
        
        getObjectsInLineOfSight();
        setObjectsTransparent();
        setObjectsNotTransparent();
    }

    private void OnDrawGizmos() {
        foreach (RaycastHit hit in crossHits) {
            Gizmos.color = Color.red;
            Gizmos.DrawLine(new Vector3(transform.position.x, transform.position.y + 2, transform.position.z), hit.point);
            Gizmos.color = Color.magenta;
            Gizmos.DrawWireSphere(hit.point, 2);
        }
    }

}
