import ApiStatus from "./ApiStatus";
import ApiList from "./ApiList";

export default function Dashboard() {
  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div className="md:col-span-2">
        <ApiList />
      </div>
      <div>
        <ApiStatus />
      </div>
    </div>
  );
}
