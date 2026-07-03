import { useAuth } from '../context/AuthContext';

export default function HomePage() {
  const { user } = useAuth();

  return (
    <div className="max-w-6xl mx-auto px-6 py-12">
      <div className="bg-white rounded-xl border border-border p-8 shadow-sm">
        <h1 className="text-2xl font-semibold text-text-primary">
          Welcome, {user?.name || user?.email}
        </h1>
        <p className="mt-2 text-text-secondary">
          You're successfully logged in to ShopMicro.
        </p>

        <div className="mt-8 grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="border border-border rounded-lg p-5">
            <div className="text-sm font-medium text-text-secondary mb-1">Email</div>
            <div className="text-sm text-text-primary">{user?.email}</div>
          </div>
          <div className="border border-border rounded-lg p-5">
            <div className="text-sm font-medium text-text-secondary mb-1">User ID</div>
            <div className="text-sm text-text-primary font-mono truncate">{user?.sub}</div>
          </div>
          <div className="border border-border rounded-lg p-5">
            <div className="text-sm font-medium text-text-secondary mb-1">Roles</div>
            <div className="flex flex-wrap gap-1.5 mt-0.5">
              {user?.roles
                ?.filter((r) => !r.startsWith('default-') && r !== 'offline_access' && r !== 'uma_authorization')
                .map((role) => (
                  <span
                    key={role}
                    className="inline-block bg-primary/10 text-primary text-xs font-medium px-2.5 py-1 rounded-full"
                  >
                    {role}
                  </span>
                ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
