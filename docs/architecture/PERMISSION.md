## 2. Chi Tiết Các Entity


**Các thuộc tính:**
| Thuộc tính | Loại | Mô tả |
|-----------|------|-------|
| `id` | Long | Primary Key, auto-increment |
| `name` | String | Tên mô tả quyền (VD: "Create a company") |
| `apiPath` | String | Đường dẫn API (VD: "/api/v1/companies", "/api/v1/companies/{id}") |
| `method` | String | HTTP Method: GET, POST, PUT, DELETE |
| `module` | String | Module thuộc về: COMPANIES, JOBS, ROLES, USERS... |
| `createdAt` | Instant | Thời gian tạo |
| `updatedAt` | Instant | Thời gian cập nhật |
| `createdBy` | String | Người tạo |
| `updatedBy` | String | Người cập nhật |

**Mối quan hệ:**
```java
@ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
private List<Role> roles;  // Các Role nào có Permission này
```

**Ví dụ:**
```
Permission: id=1, name="Create a company", apiPath="/api/v1/companies", method="POST", module="COMPANIES"
Permission: id=2, name="Get companies", apiPath="/api/v1/companies", method="GET", module="COMPANIES"
Permission: id=3, name="Update a company", apiPath="/api/v1/companies", method="PUT", module="COMPANIES"
```

### 2.2. Role Entity

**Các thuộc tính:**
| Thuộc tính | Loại | Mô tả |
|-----------|------|-------|
| `id` | Long | Primary Key |
| `name` | String | Tên Role: SUPER_ADMIN, USER... |
| `description` | String | Mô tả chi tiết về role |
| `active` | Boolean | Trạng thái hoạt động của role |
| `createdAt` | Instant | Thời gian tạo |
| `updatedAt` | Instant | Thời gian cập nhật |
| `createdBy` | String | Người tạo |
| `updatedBy` | String | Người cập nhật |

**Mối quan hệ:**
```java
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(name = "permission_role", 
           joinColumns = @JoinColumn(name = "role_id"), 
           inverseJoinColumns = @JoinColumn(name = "permission_id"))
private List<Permission> permissions;  // Danh sách permissions của role này

@OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
List<User> users;  // Danh sách users có role này
```

### 2.3. User Entity

**Mối quan hệ quan trọng:**
```java
@ManyToOne
@JoinColumn(name = "role_id")
private Role role;  // User chỉ có một role
```

---
4. Triển Khai Permission & Role
4.1 Repository Interfaces
// PermissionRepository.java
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);
    Permission findByNameAndApiPathAndMethod(String name, String apiPath, String method);
}

// RoleRepository.java
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
    boolean existsByNameAndIdNot(String name, long id);
}
4.2 PermissionService
@Service
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
    
    // Kiểm tra permission đã tồn tại
    public boolean isPermissionExist(Permission p) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
            p.getModule(),
            p.getApiPath(),
            p.getMethod()
        );
    }
    
    // Lấy permission theo ID
    public Permission fetchById(long id) {
        return this.permissionRepository.findById(id).orElse(null);
    }
    
    // Tạo permission mới
    public Permission create(Permission p) {
        if (this.isPermissionExist(p)) {
            throw new RuntimeException("Permission already exists");
        }
        return this.permissionRepository.save(p);
    }
    
    // Cập nhật permission
    public Permission update(Permission p) {
        Permission existing = this.fetchById(p.getId());
        if (existing == null) return null;
        
        existing.setName(p.getName());
        existing.setApiPath(p.getApiPath());
        existing.setMethod(p.getMethod());
        existing.setModule(p.getModule());
        
        return this.permissionRepository.save(existing);
    }
    
    // Xóa permission
    public void delete(long id) {
        Permission perm = this.fetchById(id);
        if (perm != null) {
            // Xóa relationship với roles
            perm.getRoles().forEach(role -> role.getPermissions().remove(perm));
            this.permissionRepository.delete(perm);
        }
    }
    
    // Lấy danh sách permissions với phân trang
    public ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> page = this.permissionRepository.findAll(spec, pageable);
        
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        
        result.setMeta(meta);
        result.setResult(page.getContent());
        return result;
    }
}
4.3 RoleService
@Service
public class RoleService {
    
    private final RoleRepository roleRepository;
    
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    // Tạo role mới
    public Role create(Role role) {
        if (this.roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role already exists");
        }
        role.setActive(true);
        return this.roleRepository.save(role);
    }
    
    // Cập nhật role
    public Role update(Role role) {
        Role existing = this.roleRepository.findById(role.getId()).orElse(null);
        if (existing == null) return null;
        
        existing.setName(role.getName());
        existing.setDescription(role.getDescription());
        existing.setActive(role.isActive());
        existing.setPermissions(role.getPermissions());
        
        return this.roleRepository.save(existing);
    }
    
    // Xóa role
    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }
    
    // Lấy role theo name
    public Role findByName(String name) {
        return this.roleRepository.findByName(name);
    }
    
    // Lấy role theo ID
    public Role findById(long id) {
        return this.roleRepository.findById(id).orElse(null);
    }
    
    // Gán permissions cho role
    public Role assignPermissions(long roleId, List<Permission> permissions) {
        Role role = this.findById(roleId);
        if (role != null) {
            role.setPermissions(permissions);
            return this.roleRepository.save(role);
        }
        return null;
    }
}
4.4 PermissionController
@RestController
@RequestMapping("/api/v1/permissions")
@Tag(name = "Permissions")
public class PermissionController {
    
    private final PermissionService permissionService;
    
    @PostMapping
    @ApiMessage("Create permission successfully")
    public ResponseEntity<Permission> create(@RequestBody @Valid Permission permission) {
        return ResponseEntity.ok(this.permissionService.create(permission));
    }
    
    @PutMapping
    @ApiMessage("Update permission successfully")
    public ResponseEntity<Permission> update(@RequestBody @Valid Permission permission) {
        Permission updated = this.permissionService.update(permission);
        if (updated == null) {
            throw new IdInvalidException("Permission not found");
        }
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @ApiMessage("Delete permission successfully")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        this.permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @ApiMessage("Get permission by id")
    public ResponseEntity<Permission> getById(@PathVariable long id) {
        Permission perm = this.permissionService.fetchById(id);
        if (perm == null) {
            throw new IdInvalidException("Permission not found");
        }
        return ResponseEntity.ok(perm);
    }
    
    @GetMapping
    @ApiMessage("Get all permissions")
    public ResponseEntity<ResultPaginationDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Permission> spec = Specification.where(null);
        return ResponseEntity.ok(this.permissionService.getPermissions(spec, pageable));
    }
}
4.5 RoleController
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles")
public class RoleController {
    
    private final RoleService roleService;
    
    @PostMapping
    @ApiMessage("Create role successfully")
    public ResponseEntity<Role> create(@RequestBody @Valid Role role) {
        return ResponseEntity.ok(this.roleService.create(role));
    }
    
    @PutMapping
    @ApiMessage("Update role successfully")
    public ResponseEntity<Role> update(@RequestBody @Valid Role role) {
        Role updated = this.roleService.update(role);
        if (updated == null) {
            throw new IdInvalidException("Role not found");
        }
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @ApiMessage("Delete role successfully")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        this.roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @ApiMessage("Get role by id")
    public ResponseEntity<Role> getById(@PathVariable long id) {
        Role role = this.roleService.findById(id);
        if (role == null) {
            throw new IdInvalidException("Role not found");
        }
        return ResponseEntity.ok(role);
    }
    
    @GetMapping
    @ApiMessage("Get all roles")
    public ResponseEntity<List<Role>> getAll() {
        return ResponseEntity.ok(this.roleService.findAll());
    }
}
5. Kiểm Tra Quyền (Authorization)
5.1 PermissionInterceptor
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    
    @Autowired
    private UserService userService;
    
    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        
        // Lấy path pattern từ request
        String path = (String) request.getAttribute(
            HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String httpMethod = request.getMethod();
        
        System.out.println(">>> PermissionInterceptor.preHandle()");
        System.out.println(">>> Path: " + path);
        System.out.println(">>> Method: " + httpMethod);
        
        // Lấy email của user hiện tại từ SecurityContext
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        
        if (email != null && !email.isEmpty()) {
            // Lấy user từ database
            User user = this.userService.findByEmail(email);
            
            if (user != null) {
                Role role = user.getRole();
                
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    
                    // Kiểm tra: có permission nào match với path + method không?
                    boolean hasPermission = permissions.stream()
                        .anyMatch(permission -> 
                            permission.getApiPath().equals(path) &&
                            permission.getMethod().equals(httpMethod)
                        );
                    
                    if (!hasPermission) {
                        throw new PermissionException(
                            "You do not have permission to access this endpoint");
                    }
                } else {
                    throw new PermissionException(
                        "User has no role assigned");
                }
            }
        }
        
        return true;
    }
}
5.2 PermissionInterceptorConfiguration
@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    
    @Autowired
    private PermissionInterceptor permissionInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
            .addPathPatterns("/api/v1/**")
            .excludePathPatterns(
                "/api/v1/auth/login",
                "/api/v1/auth/register",
                "/api/v1/auth/refresh-token",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            );
    }
}
5.3 Exception Handler
@RestControllerAdvice
public class GlobalException {
    
    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<ApiResponse<?>> handlePermissionException(PermissionException ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setStatusCode(403);  // Forbidden
        response.setMessage(ex.getMessage());
        response.setData(null);
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    @ExceptionHandler(IdInvalidException.class)
    public ResponseEntity<ApiResponse<?>> handleIdInvalidException(IdInvalidException ex) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setStatusCode(400);  // Bad Request
        response.setMessage(ex.getMessage());
        response.setData(null);
        
        return ResponseEntity.badRequest().body(response);
    }
}
